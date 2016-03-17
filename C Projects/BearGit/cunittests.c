#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <unistd.h>
#include <CUnit/Basic.h>
#include "beargit.h"
#include "util.h"

/* printf/fprintf calls in this tester will NOT go to file. */

#undef printf
#undef fprintf

/* The suite initialization function.
 * You'll probably want to delete any leftover files in .beargit from previous
 * tests, along with the .beargit directory itself.
 *
 * You'll most likely be able to share this across suites.
 */
int init_suite(void)
{
    // preps to run tests by deleting the .beargit directory if it exists
    fs_force_rm_beargit_dir();
    unlink("TEST_STDOUT");
    unlink("TEST_STDERR");
    return 0;
}

/* You can also delete leftover files after a test suite runs, but there's
 * no need to duplicate code between this and init_suite 
 */
int clean_suite(void)
{
    return 0;
}

/* Simple test of fread().
 * Reads the data previously written by testFPRINTF()
 * and checks whether the expected characters are present.
 * Must be run after testFPRINTF().
 */
void simple_sample_test(void)
{
    // This is a very basic test. Your tests should likely do more than this.
    // We suggest checking the outputs of printfs/fprintfs to both stdout
    // and stderr. To make this convenient for you, the tester replaces
    // printf and fprintf with copies that write data to a file for you
    // to access. To access all output written to stdout, you can read 
    // from the "TEST_STDOUT" file. To access all output written to stderr,
    // you can read from the "TEST_STDERR" file.
    int retval;
    retval = beargit_init();
    CU_ASSERT(0==retval);
    retval = beargit_add("asdf.txt");
    CU_ASSERT(0==retval);
}

struct commit {
  char msg[MSG_SIZE];
  struct commit* next;
};


void free_commit_list(struct commit** commit_list) {
  if (*commit_list) {
    free_commit_list(&((*commit_list)->next));
    free(*commit_list);
  }

  *commit_list = NULL;
}

void run_commit(struct commit** commit_list, const char* msg) {
    int retval = beargit_commit(msg);
    CU_ASSERT(0==retval);

    struct commit* new_commit = (struct commit*)malloc(sizeof(struct commit));
    new_commit->next = *commit_list;
    strcpy(new_commit->msg, msg);
    *commit_list = new_commit;
}

void simple_log_test(void)
{
    struct commit* commit_list = NULL;
    int retval;
    retval = beargit_init();
    CU_ASSERT(0==retval);
    FILE* asdf = fopen("asdf.txt", "w");
    fclose(asdf);
    retval = beargit_add("asdf.txt");
    CU_ASSERT(0==retval);
    run_commit(&commit_list, "THIS IS BEAR TERRITORY!1");
    run_commit(&commit_list, "THIS IS BEAR TERRITORY!2");
    run_commit(&commit_list, "THIS IS BEAR TERRITORY!3");

    retval = beargit_log(10);
    CU_ASSERT(0==retval);

    struct commit* cur_commit = commit_list;

    const int LINE_SIZE = 512;
    char line[LINE_SIZE];

    FILE* fstdout = fopen("TEST_STDOUT", "r");
    CU_ASSERT_PTR_NOT_NULL(fstdout);

    while (cur_commit != NULL) {
      char refline[LINE_SIZE];

      // First line is commit -- don't check the ID.
      CU_ASSERT_PTR_NOT_NULL(fgets(line, LINE_SIZE, fstdout));
      CU_ASSERT(!strncmp(line,"commit", strlen("commit")));

      // Second line is msg
      sprintf(refline, "   %s\n", cur_commit->msg);
      CU_ASSERT_PTR_NOT_NULL(fgets(line, LINE_SIZE, fstdout));
      CU_ASSERT_STRING_EQUAL(line, refline);

      // Third line is empty
      CU_ASSERT_PTR_NOT_NULL(fgets(line, LINE_SIZE, fstdout));
      CU_ASSERT(!strcmp(line,"\n"));

      cur_commit = cur_commit->next;
    }

    CU_ASSERT_PTR_NULL(fgets(line, LINE_SIZE, fstdout));

    // It's the end of output
    CU_ASSERT(feof(fstdout));
    fclose(fstdout);

    free_commit_list(&commit_list);
}



/* Our own tests.*/

/* Commit and checkout test. 
    We add a file and commit it, then add another and commit again.
    We then checkout the first commit and make sure that the file
    included in the second commit has been removed from the index. */

void commit_checkout_test(void)
{
    struct commit* commit_list = NULL;
    int retval;

    retval = beargit_init();
    CU_ASSERT(0==retval);
    /* Adding a text file to index. */
    FILE* dog = fopen("dog.txt", "w");
    fclose(dog);
    retval = beargit_add("dog.txt");
    CU_ASSERT(0==retval);
    /* Status prints to stdout. */
    retval = beargit_status();
    FILE* fstdout = fopen("TEST_STDOUT", "r");
    CU_ASSERT_PTR_NOT_NULL(fstdout);
    fclose(fstdout);
    CU_ASSERT(0==retval);
    retval = beargit_log(10);
    CU_ASSERT(1==retval);
    FILE* fstderr = fopen("TEST_STDERR", "r");
    CU_ASSERT_PTR_NOT_NULL(fstderr);
    fclose(fstderr);


    /* Successful commit. */
    run_commit(&commit_list, "Starting text THIS IS BEAR TERRITORY! ending text.");
    retval = beargit_log(10);
    CU_ASSERT(0==retval);

    FILE* cat = fopen("cat.txt", "w");
    fclose(cat);
    retval = beargit_add("cat.txt");
    CU_ASSERT(0==retval);

    run_commit(&commit_list, "Second starting text THIS IS BEAR TERRITORY! second ending text.");
    retval = beargit_log(10);
    CU_ASSERT(0==retval);

    retval = beargit_checkout("9bb6d2935d7064579911b9353f90a1d359ea6c41", 0);
    CU_ASSERT(0==retval)

    /* cat file should have been removed from the index, so attempt to remove should error. */
    retval = beargit_rm("cat.txt");
    CU_ASSERT(1==retval);
    fstderr = fopen("TEST_STDERR", "r");
    CU_ASSERT_PTR_NOT_NULL(fstderr);
    fclose(fstderr);

    /* Checking out master branch should return the cat file to the index. */
    retval = beargit_checkout("master", 0);
    CU_ASSERT(0==retval);
    /* Should be able to remove cat file from the index. */
    retval = beargit_rm("cat.txt");
    CU_ASSERT(0==retval);

}

/* Remove and checking out new branch error test.
    We attempt to remove a file which is not being tracked in order to
    check that the proper error messages are supplied.
    We then check that an added file can indeed be removed from the
    index. */
void remove_test(void) {
  struct commit* commit_list = NULL;
  int retval;

  retval = beargit_init();
  CU_ASSERT(0==retval);
  FILE* dog = fopen("dog.txt", "w");
  fclose(dog);
  retval = beargit_add("dog.txt");
  CU_ASSERT(0==retval);
  run_commit(&commit_list, "THIS IS BEAR TERRITORY!1");
  run_commit(&commit_list, "THIS IS BEAR TERRITORY!2");
  run_commit(&commit_list, "THIS IS BEAR TERRITORY!3");

  retval = beargit_log(10);
  CU_ASSERT(0==retval);

  FILE* fstdout = fopen("TEST_STDOUT", "r");
  CU_ASSERT_PTR_NOT_NULL(fstdout);

  char line[512];

  retval=beargit_rm("cat.txt");
  CU_ASSERT(1==retval);
  FILE* fstderr = fopen("TEST_STDERR", "r");
  CU_ASSERT_PTR_NOT_NULL(fstderr);
  CU_ASSERT_PTR_NOT_NULL(fgets(line, 512, fstderr));
  CU_ASSERT_STRING_EQUAL(line, "ERROR:  File cat.txt not tracked.\n");
  fclose(fstderr);

  retval=beargit_rm("dog.txt");
  CU_ASSERT(0==retval);

}





/* The main() function for setting up and running the tests.
 * Returns a CUE_SUCCESS on successful running, another
 * CUnit error code on failure.
 */
int cunittester()
{
   CU_pSuite pSuite = NULL;
   CU_pSuite pSuite2 = NULL;
   CU_pSuite pSuite3 = NULL;
   CU_pSuite pSuite4 = NULL;

   /* initialize the CUnit test registry */
   if (CUE_SUCCESS != CU_initialize_registry())
      return CU_get_error();

   /* add a suite to the registry */
   pSuite = CU_add_suite("Suite_1", init_suite, clean_suite);
   if (NULL == pSuite) {
      CU_cleanup_registry();
      return CU_get_error();
   }

   /* Add tests to the Suite #1 */
   if (NULL == CU_add_test(pSuite, "Simple Test #1", simple_sample_test))
   {
      CU_cleanup_registry();
      return CU_get_error();
   }

   pSuite2 = CU_add_suite("Suite_2", init_suite, clean_suite);
   if (NULL == pSuite2) {
      CU_cleanup_registry();
      return CU_get_error();
   }

   /* Add tests to the Suite #2 */
   if (NULL == CU_add_test(pSuite2, "Log output test", simple_log_test))
   {
      CU_cleanup_registry();
      return CU_get_error();
   }

  /* Running our own tests. */

  /* add a suite to the registry */
  pSuite3 = CU_add_suite("Suite_3", init_suite, clean_suite);
  if (NULL == pSuite3) {
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* Add tests to the Suite #3 */
  if (NULL == CU_add_test(pSuite3, "Commit and checkout test", commit_checkout_test))
  {
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* add a suite to the registry */
  pSuite4 = CU_add_suite("Suite_4", init_suite, clean_suite);
  if (NULL == pSuite4) {
    CU_cleanup_registry();
    return CU_get_error();
  }

  /* Add tests to the Suite #4 */
  if (NULL == CU_add_test(pSuite4, "Remove test", remove_test))
  {
    CU_cleanup_registry();
    return CU_get_error();
  }




   /* Run all tests using the CUnit Basic interface */
   CU_basic_set_mode(CU_BRM_VERBOSE);
   CU_basic_run_tests();
   CU_cleanup_registry();
   return CU_get_error();
}

