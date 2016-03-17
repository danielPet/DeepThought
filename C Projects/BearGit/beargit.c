/*this was inside proj1-aez-afd*/

#include <stdio.h>
#include <string.h>

#include <unistd.h>
#include <sys/stat.h>

#include "beargit.h"
#include "util.h"

/* Implementation Notes:
 *
 * - Functions return 0 if successful, 1 if there is an error.
 * - All error conditions in the function description need to be implemented
 *   and written to stderr. We catch some additional errors for you in main.c.
 * - Output to stdout needs to be exactly as specified in the function description.
 * - Only edit this file (beargit.c)
 * - Here are some of the helper functions from util.h:
 *   * fs_mkdir(dirname): create directory <dirname>
 *   * fs_rm(filename): delete file <filename>
 *   * fs_mv(src,dst): move file <src> to <dst>, overwriting <dst> if it exists
 *   * fs_cp(src,dst): copy file <src> to <dst>, overwriting <dst> if it exists
 *   * write_string_to_file(filename,str): write <str> to filename (overwriting contents)
 *   * read_string_from_file(filename,str,size): read a string of at most <size> (incl.
 *     NULL character) from file <filename> and store it into <str>. Note that <str>
 *     needs to be large enough to hold that string.
 *  - You NEED to test your code. The autograder we provide does not contain the
 *    full set of tests that we will run on your code. See "Step 5" in the project spec.
 */

/* beargit init
 *
 * - Create .beargit directory
 * - Create empty .beargit/.index file
 * - Create .beargit/.prev file containing 0..0 commit id
 *
 * Output (to stdout):
 * - None if successful
 */

int beargit_init(void) {
  fs_mkdir(".beargit");

  FILE* findex = fopen(".beargit/.index", "w");
  fclose(findex);

  FILE* fbranches = fopen(".beargit/.branches", "w");
  fprintf(fbranches, "%s\n", "master");
  fclose(fbranches);

  write_string_to_file(".beargit/.prev", "0000000000000000000000000000000000000000");
  write_string_to_file(".beargit/.current_branch", "master");

  return 0;
}



/* beargit add <filename>
 *
 * - Append filename to list in .beargit/.index if it isn't in there yet
 *
 * Possible errors (to stderr):
 * >> ERROR:  File <filename> has already been added.
 *
 * Output (to stdout):
 * - None if successful
 */

int beargit_add(const char* filename) {
  FILE* findex = fopen(".beargit/.index", "r");
  FILE *fnewindex = fopen(".beargit/.newindex", "w");

  char line[FILENAME_SIZE];
  while(fgets(line, sizeof(line), findex)) {
    strtok(line, "\n");
    if (strcmp(line, filename) == 0) {
      fprintf(stderr, "ERROR:  File %s has already been added.\n", filename);
      fclose(findex);
      fclose(fnewindex);
      fs_rm(".beargit/.newindex");
      return 3;
    }

    fprintf(fnewindex, "%s\n", line);
  }

  fprintf(fnewindex, "%s\n", filename);
  fclose(findex);
  fclose(fnewindex);

  fs_mv(".beargit/.newindex", ".beargit/.index");

  return 0;
}

/* beargit status
 *
 * See "Step 1" in the project spec.
 *
 */

int beargit_status() {
  FILE* findex = fopen(".beargit/.index", "r");
  char *filename;
  char line[FILENAME_SIZE];
  int i = 0;

  fprintf(stdout, "Tracked files:\n\n");

  while(fgets(line, sizeof(line), findex)) {
    filename = strtok(line, "\n");
    if (filename != NULL) {
      fprintf(stdout, "%s\n", filename);
      i += 1;
    }

  }
  fclose(findex);
  fprintf(stdout, "\nThere are %d files total.\n", i);
  
  return 0;
}

/* beargit rm <filename>
 *
 * See "Step 2" in the project spec.
 *
 */

int beargit_rm(const char* filename) {
  FILE* findex = fopen(".beargit/.index", "r");
  FILE *fnewindex = fopen(".beargit/.newindex", "w");
  int tracked = 0;
  char line[FILENAME_SIZE];

  while(fgets(line, sizeof(line), findex)) {
    strtok(line, "\n");
    if (strcmp(line, filename) == 0) {
      tracked = 1;
    } else {
      fprintf(fnewindex, "%s\n", line);
    }
  }

  if (!tracked) {
    fprintf(stderr, "ERROR:  File %s not tracked.\n", filename);
    fclose(findex);
    fclose(fnewindex);
    fs_rm(".beargit/.newindex");
    return 1;
  }
  
  fs_mv(".beargit/.newindex", ".beargit/.index");
  fclose(findex);
  fclose(fnewindex);
  return 0;
}

/* beargit commit -m <msg>
 *
 * See "Step 3" in the project spec.
 *
 */


const char* go_bears = "THIS IS BEAR TERRITORY!";

int is_commit_msg_ok(const char* msg) {
  int i = 0, j;

  while(msg[i]) { /* Loop length of msg. */
    if(msg[i] == 'T') { /* If msg has char 'T', triggers message check loop. */
      for(j = 0; j < 23; j += 1) {
        if(msg[i] == go_bears[j]) i += 1;
        else break; /* If chars don't match, leave the FOR loop. */
        if(j==22) return 1; /* If all chars have been checked and pass, return 0. */
      }
    } else i += 1;
  }
  return 0;
}

/* Use next_commit_id to fill in the rest of the commit ID.
 *
 * Hints:
 * You will need a destination string buffer to hold your next_commit_id, before you copy it back to commit_id
 * You will need to use a function we have provided for you.
 */

void next_commit_id(char* commit_id) {
  int i;
  char next_commit_id[COMMIT_ID_SIZE];
  cryptohash(commit_id, next_commit_id);
  for (i = 0; i < COMMIT_ID_SIZE; i += 1) {
    commit_id[i] = next_commit_id[i];
  }
  return;
}

int beargit_commit(const char* msg) {
  if (!is_commit_msg_ok(msg)) {
    fprintf(stderr, "ERROR:  Message must contain \"%s\"\n", go_bears);
    return 1;
  }

  char current_branch[BRANCHNAME_SIZE];
  read_string_from_file(".beargit/.current_branch", current_branch, BRANCHNAME_SIZE);
  if (strcmp(current_branch, "") == 0) {
    fprintf(stderr, "ERROR:  Need to be on HEAD of a branch to commit.\n");
    return 1;
  }

  int i;
  char commit_id[COMMIT_ID_SIZE];
  read_string_from_file(".beargit/.prev", commit_id, COMMIT_ID_SIZE);
  /* Alter commit ID with branch name. */
  for (i = 0; (i < strlen(current_branch)) && (i < COMMIT_ID_SIZE); i += 1) {
    commit_id[i] = current_branch[i];
  }

  next_commit_id(commit_id);

  char new_dir[FILENAME_SIZE];
  snprintf(new_dir, FILENAME_SIZE, ".beargit/%s/", commit_id);

  char new_index[FILENAME_SIZE]; /* .beargit/<newid>/.index */
  snprintf(new_index, FILENAME_SIZE, ".beargit/%s/.index", commit_id);

  char new_prev[FILENAME_SIZE]; /* .beargit/<newid>/.prev */
  snprintf(new_prev, FILENAME_SIZE, ".beargit/%s/.prev", commit_id);

  fs_mkdir(new_dir);
  fs_cp(".beargit/.index", new_index);
  fs_cp(".beargit/.prev", new_prev);

  FILE* findex = fopen(".beargit/.index", "r"); /* Copying files over.*/
  char *filename;
  char line[FILENAME_SIZE];
  char dest_file[FILENAME_SIZE];
  while(fgets(line, sizeof(line), findex)) {
    filename = strtok(line, "\n");
    snprintf(dest_file, FILENAME_SIZE, "%s%s", new_dir, filename);
    fs_cp(filename, dest_file);
  }
  fclose(findex);

  char msg_file[FILENAME_SIZE]; /* .beargit/<newid>/.msg */
  snprintf(msg_file, FILENAME_SIZE, ".beargit/%s/.msg", commit_id);
  write_string_to_file(msg_file, msg);

  write_string_to_file(".beargit/.prev", commit_id);
  
  return 0;
}

/* beargit log
 *
 * See "Step 4" in the project spec.
 *
 */

int beargit_log(int limit) {
  char current_commit[COMMIT_ID_SIZE];
  read_string_from_file(".beargit/.prev", current_commit, COMMIT_ID_SIZE);
  char prev_dir[FILENAME_SIZE];
  char msg_file[FILENAME_SIZE]; /* .beargit/<newid>/.msg */
  char current_message[MSG_SIZE];

  if (strcmp(current_commit, "0000000000000000000000000000000000000000") == 0) {
    fprintf(stderr, "%s\n", "ERROR:  There are no commits.");
    return 1;
  }
  
  int i;
  for (i = 0; i < limit; i += 1) {
    if (strcmp(current_commit, "0000000000000000000000000000000000000000") == 0) {
      break;
    } else {
      snprintf(msg_file, FILENAME_SIZE, ".beargit/%s/.msg", current_commit);
      read_string_from_file(msg_file, current_message, MSG_SIZE);
      fprintf(stdout, "commit %s\n   %s\n\n", current_commit, current_message);
      /* Get next ID */
      snprintf(prev_dir, FILENAME_SIZE, ".beargit/%s/.prev", current_commit);
      read_string_from_file(prev_dir, current_commit, COMMIT_ID_SIZE);
    }
  }
  return 0;
}

// This helper function returns the branch number for a specific branch, or
// returns -1 if the branch does not exist.
int get_branch_number(const char* branch_name) {
  FILE* fbranches = fopen(".beargit/.branches", "r");

  int branch_index = -1;
  int counter = 0;
  char line[FILENAME_SIZE];
  while(fgets(line, sizeof(line), fbranches)) {
    strtok(line, "\n");
    if (strcmp(line, branch_name) == 0) {
      branch_index = counter;
    }
    counter++;
  }

  fclose(fbranches);

  return branch_index;
}

/* beargit branch
 *
 * See "Step 5" in the project spec.
 *
 */

int beargit_branch() {
  FILE* fbranches = fopen(".beargit/.branches", "r");
  char *branchname;
  char line[BRANCHNAME_SIZE];
  char current_branch[BRANCHNAME_SIZE];

  read_string_from_file(".beargit/.current_branch", current_branch, BRANCHNAME_SIZE);

  while(fgets(line, sizeof(line), fbranches)) {
    branchname = strtok(line, "\n");
    if (strcmp(branchname, current_branch) == 0) {
      fprintf(stdout, "*  %s\n", branchname);
    } else {
      fprintf(stdout, "   %s\n", branchname);
    }
  }
  fclose(fbranches);
  
  return 0;
}

/* beargit checkout
 *
 * See "Step 6" in the project spec.
 *
 */

int checkout_commit(const char* commit_id) {
  /* Delete all files currently in index of working directory. */
  FILE* findex = fopen(".beargit/.index", "r");
  char *filename;
  char line[FILENAME_SIZE];
  while(fgets(line, sizeof(line), findex)) {
    filename = strtok(line, "\n");
    if (fs_check_dir_exists(filename)) {
      fs_rm(filename);
    }
  }
  fclose(findex);

  /* Write commit into previous file. */
  write_string_to_file(".beargit/.prev", commit_id);
  /* Clear the index. */
  write_string_to_file(".beargit/.index", "");

  if (strcmp(commit_id, "0000000000000000000000000000000000000000") != 0) {

    /* Replace index with commit's index. */
    char commit_index[FILENAME_SIZE];
    snprintf(commit_index, FILENAME_SIZE, ".beargit/%s/.index", commit_id);


    char commit_dir[FILENAME_SIZE];
    snprintf(commit_dir, FILENAME_SIZE, ".beargit/%s/", commit_id);

    FILE* findex = fopen(".beargit/.index", "r"); /* Copying files over.*/
    FILE* fcommit_index = fopen(commit_index, "r");
    char source_file[FILENAME_SIZE]; /* Files from the commit. */
    while(fgets(line, sizeof(line), fcommit_index)) {
      filename = strtok(line, "\n");
      snprintf(source_file, FILENAME_SIZE, "%s%s", commit_dir, filename);
      fs_cp(source_file, filename);
    }
    fs_cp(commit_index, ".beargit/.index");
    fclose(findex);
    fclose(fcommit_index);
  }

  return 0;
}

int is_it_a_commit_id(const char* commit_id) {
  char dir_name[FILENAME_SIZE];
  snprintf(dir_name, FILENAME_SIZE, ".beargit/%s", commit_id);
  return fs_check_dir_exists(dir_name);
}

int beargit_checkout(const char* arg, int new_branch) {
  // Get the current branch
  char current_branch[BRANCHNAME_SIZE];
  read_string_from_file(".beargit/.current_branch", current_branch, BRANCHNAME_SIZE); /* Bug1: Second arg was a string literal here. */

  // If not detached, leave the current branch by storing the current HEAD into that branch's file...
  if (strlen(current_branch)) {
    char current_branch_file[BRANCHNAME_SIZE+20];
    sprintf(current_branch_file, ".beargit/.branch_%s", current_branch);
    fs_cp(".beargit/.prev", current_branch_file);
  }

  // Check whether the argument is a commit ID. If yes, we just change to detached mode
  // without actually having to change into any other branch.
  if (is_it_a_commit_id(arg)) {
    char commit_dir[FILENAME_SIZE] = ".beargit/";
    strcat(commit_dir, arg);
    // ...and setting the current branch to none (i.e., detached).
    write_string_to_file(".beargit/.current_branch", "");

    return checkout_commit(arg);
  }

  // Read branches file (giving us the HEAD commit id for that branch).
  int branch_exists = (get_branch_number(arg) >= 0);

  // Check for errors.
  if (branch_exists && new_branch) { /* Bug2a: Was (!(!branch_exists || new_branch)) */
    fprintf(stderr, "ERROR:  A branch named %s already exists.\n", arg);
    return 1;
  } else if (!branch_exists && !new_branch) { /* Bug2b: Was (!branch_exists && new_branch) */
    fprintf(stderr, "ERROR:  No branch or commit %s exists.\n", arg);
    return 1;
  }

  // Just a better name, since we now know the argument is a branch name.
  const char* branch_name = arg;

  // File for the branch we are changing into.
  char branch_file[FILENAME_SIZE];
  snprintf(branch_file, FILENAME_SIZE, ".beargit/.branch_%s", branch_name); /* Bug3: Was char* branch_file = ".beargit/.branch_"; */

  // Update the branch file if new branch is created (now it can't go wrong anymore)
  if (new_branch) {
    FILE* fbranches = fopen(".beargit/.branches", "a");
    fprintf(fbranches, "%s\n", branch_name);
    fclose(fbranches);
    fs_cp(".beargit/.prev", branch_file);
  }

  write_string_to_file(".beargit/.current_branch", branch_name);

  // Read the head commit ID of this branch.
  char branch_head_commit_id[COMMIT_ID_SIZE];
  read_string_from_file(branch_file, branch_head_commit_id, COMMIT_ID_SIZE);
  // Check out the actual commit.
  return checkout_commit(branch_head_commit_id);
}

/* beargit reset
 *
 * See "Step 7" in the project spec.
 *
 */

int beargit_reset(const char* commit_id, const char* filename) {
  if (!is_it_a_commit_id(commit_id)) {
      fprintf(stderr, "ERROR:  Commit %s does not exist.\n", commit_id);
      return 1;
  }

  char current_dir_file[FILENAME_SIZE];
  strcat(current_dir_file, filename);

  char commit_dir_file[FILENAME_SIZE];
  snprintf(commit_dir_file, FILENAME_SIZE, ".beargit/%s/%s", commit_id, filename);
  FILE *commit_file = fopen(commit_dir_file, "r");
  // Check if the file is in the commit directory
  if(commit_file != NULL) {
  // Copy the file to the current working directory    
    fs_cp(commit_dir_file, current_dir_file);
    fclose(commit_file);
  } else {
    fprintf(stderr, "ERROR:  %s is not in the index of commit %s.\n", filename, commit_id);
    fclose(commit_file);
    return 1;
  }

  // Add the file if it wasn't already there
  FILE* findex = fopen(".beargit/.index", "r");
  FILE *fnewindex = fopen(".beargit/.newindex", "w");

  char line[FILENAME_SIZE];
  int in_index = 0;
  while(fgets(line, sizeof(line), findex)) {
    strtok(line, "\n");
    if (strcmp(line, filename) == 0) {
      in_index = 1;
    }
    fprintf(fnewindex, "%s\n", line);
  }

  if (!in_index) {
    fprintf(fnewindex, "%s\n", filename);
    fs_mv(".beargit/.newindex", ".beargit/.index");
  }
  
  fclose(findex);
  fclose(fnewindex);

  return 0;
}

/* beargit merge
 *
 * See "Step 8" in the project spec.
 *
 */

int beargit_merge(const char* arg) {
  // Get the commit_id or throw an error
  char commit_id[COMMIT_ID_SIZE];
  if (!is_it_a_commit_id(arg)) {
      if (get_branch_number(arg) == -1) {
            fprintf(stderr, "ERROR:  No branch or commit %s exists.\n", arg);
            return 1;
      }
      char branch_file[FILENAME_SIZE];
      snprintf(branch_file, FILENAME_SIZE, ".beargit/.branch_%s", arg);
      read_string_from_file(branch_file, commit_id, COMMIT_ID_SIZE);
  } else {
      snprintf(commit_id, COMMIT_ID_SIZE, "%s", arg);
  }


  char commit_index_name[FILENAME_SIZE];
  sprintf(commit_index_name, ".beargit/%s/.index", commit_id);
  char commit_filename[FILENAME_SIZE];
  char copy_filename[FILENAME_SIZE];

  FILE* commit_index = fopen(commit_index_name, "r");
  FILE* index_to_copy = fopen(".beargit/.index", "r");
  FILE* new_current_index = fopen(".beargit/.newindex", "w");  

  char commit_index_line[FILENAME_SIZE];
  char copy_index_line[FILENAME_SIZE];


  /* Copy index to new index. */
  while(fgets(copy_index_line, sizeof(copy_index_line), index_to_copy)) {
    strtok(copy_index_line, "\n");
    fprintf(new_current_index, "%s\n", copy_index_line);
  }
  fclose(index_to_copy);

  int in_index = 0;  
  /* Iterate through each line of the commit_id index and determine how you
  should copy the index file over. */
  while(fgets(commit_index_line, sizeof(commit_index_line), commit_index)) {
    strtok(commit_index_line, "\n");
    /* Reopen file each iteration. */
    FILE* current_index = fopen(".beargit/.index", "r");
    char current_index_line[FILENAME_SIZE];
    /* Check if file is in the current index, if so, set in_index to 1. */
    while(fgets(current_index_line, sizeof(current_index_line), current_index)) {
      strtok(current_index_line, "\n");
      if (strcmp(current_index_line, commit_index_line) == 0) {
        in_index = 1;
      }
    }
    if (in_index) { /* File is in the current index. */
      sprintf(commit_filename, ".beargit/%s/%s", commit_id, commit_index_line);
      sprintf(copy_filename, "%s.%s", commit_index_line, commit_id);
      fs_cp(commit_filename, copy_filename);
      fprintf(stdout, "%s conflicted copy created\n", commit_index_line);
    } else {
      sprintf(commit_filename, ".beargit/%s/%s", commit_id, commit_index_line);
      sprintf(copy_filename, "%s", commit_index_line);
      fs_cp(commit_filename, copy_filename);
      fprintf(new_current_index, "%s\n", commit_index_line); /* Add file to new index.*/
      fprintf(stdout, "%s added\n", copy_filename);
    }
    in_index = 0;
    fclose(current_index);
  }

  /* Replace old index. */
  fs_mv(".beargit/.newindex", ".beargit/.index");
  fclose(commit_index);
  fclose(new_current_index);

  return 0;
}
