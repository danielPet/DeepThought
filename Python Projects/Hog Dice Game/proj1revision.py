"""The Game of Hog."""

from dice import four_sided, six_sided, make_test_dice
from ucb import main, trace, log_current_line, interact

GOAL_SCORE = 100 # The goal of Hog is to score 100 points.

######################
# Phase 1: Simulator #
######################

def roll_dice(num_rolls, dice=six_sided):
    """Roll DICE for NUM_ROLLS times.  Return either the sum of the outcomes,
    or 1 if a 1 is rolled (Pig out). This calls DICE exactly NUM_ROLLS times.

    num_rolls:  The number of dice rolls that will be made; at least 1.
    dice:       A zero-argument function that returns an integer outcome.
    """
    # These assert statements ensure that num_rolls is a positive integer.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls > 0, 'Must roll at least once.'
    "*** YOUR CODE HERE ***"
    turn_sum = 0
    rolled_one = False
    while num_rolls > 0:
        
        single_roll = dice()
        
        if single_roll == 1:
            rolled_one = True
        else:
            turn_sum = turn_sum + single_roll
        
        num_rolls = num_rolls - 1
        
    return 1 if rolled_one else turn_sum

def take_turn(num_rolls, opponent_score, dice=six_sided):
    """Simulate a turn rolling NUM_ROLLS dice, which may be 0 (Free bacon).

    num_rolls:       The number of dice rolls that will be made.
    opponent_score:  The total score of the opponent.
    dice:            A function of no args that returns an integer outcome.
    """
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls >= 0, 'Cannot roll a negative number of dice.'
    assert num_rolls <= 10, 'Cannot roll more than 10 dice.'
    assert opponent_score < 100, 'The game should be over.'
    "*** YOUR CODE HERE ***"
    
    opp_unit = opponent_score % 10
    opp_tens = (opponent_score) // 10

    if num_rolls == 0:
        return abs(opp_tens - opp_unit) + 1
    else:
        return roll_dice(num_rolls, dice)


def select_dice(score, opponent_score):
    """Select six-sided dice unless the sum of SCORE and OPPONENT_SCORE is a
    multiple of 7, in which case select four-sided dice (Hog wild).
    """
    if (score + opponent_score) % 7 == 0:
        return four_sided
    else:
        return six_sided

def bid_for_start(bid0, bid1, goal=GOAL_SCORE):
    """Given the bids BID0 and BID1 of each player, returns three values:

    - the starting score of player 0
    - the starting score of player 1
    - the number of the player who rolls first (0 or 1)
    """
    assert bid0 >= 0 and bid1 >= 0, "Bids should be non-negative!"
    assert type(bid0) == int and type(bid1) == int, "Bids should be integers!"

    if bid0 == bid1:
        return goal, goal, 0
    if bid0 == bid1 + 5:
        return 10, 0, 0
    if bid1 == bid0 + 5:
        return 0, 10, 1
    elif bid1 > bid0:
        return bid1, bid0, 1
    else:
        return bid1, bid0, 0

def other(who):
    """Return the other player, for a player WHO numbered 0 or 1.

    >>> other(0)
    1
    >>> other(1)
    0
    """
    return 1 - who

def play(strategy0, strategy1, score0=0, score1=0, goal=GOAL_SCORE):
    """Simulate a game and return the final scores of both players, with
    Player 0's score first, and Player 1's score second.

    A strategy is a function that takes two total scores as arguments
    (the current player's score, and the opponent's score), and returns a
    number of dice that the current player will roll this turn.

    strategy0:  The strategy function for Player 0, who plays first
    strategy1:  The strategy function for Player 1, who plays second
    score0   :  The starting score for Player 0
    score1   :  The starting score for Player 1
    """
    who = 0  # Which player is about to take a turn, 0 (first) or 1 (second)
    score0, score1 = 0, 0

    while (score0 < goal) and (score1 < goal):
        
        dice = select_dice(score0, score1) # Hog Wild included.
        
        if who == 0:
            opponent_score = score1
            num_rolls = strategy0(score0, score1)
        elif who == 1:
            opponent_score = score0
            num_rolls = strategy1(score1, score0)
        
        score = take_turn(num_rolls, opponent_score, dice) # Free Bacon included.
        
        if who == 0:
            score0 = score + score0
        elif who == 1:
            score1 = score + score1

        if (score0 == 2 * score1) or (score1 == 2 * score0): # Swine Swap included.
            temp = score0
            score0 = score1
            score1 = temp

        who = other(who)

    return score0, score1

#######################
# Phase 2: Strategies #
#######################

def always_roll(n):
    """Return a strategy that always rolls N dice.

    A strategy is a function that takes two total scores as arguments
    (the current player's score, and the opponent's score), and returns a
    number of dice that the current player will roll this turn.

    >>> strategy = always_roll(5)
    >>> strategy(0, 0)
    5
    >>> strategy(99, 99)
    5
    """
    def strategy(score, opponent_score):
        return n
    return strategy

# *************Experiments************* #

def make_averaged(fn, num_samples=1000000):
    """Return a function that returns the average_value of FN when called.

    To implement this function, you will have to use *args syntax, a new Python
    feature introduced in this project.  See the project description.

    >>> dice = make_test_dice(3, 1, 5, 6)
    >>> averaged_dice = make_averaged(dice, 1000)
    >>> averaged_dice()
    3.75
    >>> make_averaged(roll_dice, 1000)(2, dice)
    6.0

    In this last example, two different turn scenarios are averaged.
    - In the first, the player rolls a 3 then a 1, receiving a score of 1.
    - In the other, the player rolls a 5 and 6, scoring 11.
    Thus, the average value is 6.0.
    """
    def function(*args):
        i = num_samples
        total = 0
        while i > 0:
            total = fn(*args) + total
            i = i - 1
        return total / num_samples
    return function

def max_scoring_num_rolls(dice=six_sided):
    """Return the number of dice (1 to 10) that gives the highest average turn
    score by calling roll_dice with the provided DICE.  Assume that dice always
    returns positive outcomes.

    >>> dice = make_test_dice(3)
    >>> max_scoring_num_rolls(dice)
    10
    """
    best_roll = 1
    old_ave = 0
    i = 1

    while i < 11:
        new_ave = make_averaged(roll_dice, 1000)(i, dice)
        if new_ave > old_ave:
            old_ave = new_ave
            best_roll = i
        i = i + 1
    
    return best_roll

def winner(strategy0, strategy1):
    """Return 0 if strategy0 wins against strategy1, and 1 otherwise."""
    score0, score1 = play(strategy0, strategy1)
    if score0 > score1:
        return 0
    else:
        return 1

def average_win_rate(strategy, baseline=always_roll(5)):
    """Return the average win rate (0 to 1) of STRATEGY against BASELINE."""
    win_rate_as_player_0 = 1 - make_averaged(winner)(strategy, baseline)
    win_rate_as_player_1 = make_averaged(winner)(baseline, strategy)
    return (win_rate_as_player_0 + win_rate_as_player_1) / 2 # Average results

def run_experiments():
    """Run a series of strategy experiments and report results."""
    if False: # Change to False when done finding max_scoring_num_rolls
        six_sided_max = max_scoring_num_rolls(six_sided)
        print('Max scoring num rolls for six-sided dice:', six_sided_max)
        four_sided_max = max_scoring_num_rolls(four_sided)
        print('Max scoring num rolls for four-sided dice:', four_sided_max)

    if False: # Change to True to test always_roll(6)
        print('always_roll(6) win rate:', average_win_rate(always_roll(6)))

    if False: # Change to True to test bacon_strategy
        print('bacon_strategy win rate:', average_win_rate(bacon_strategy))

    if False: # Change to True to test swap_strategy
        print('swap_strategy win rate:', average_win_rate(swap_strategy))

    if True: # Change to True to test final_strategy
        print('final_strategy win rate:', average_win_rate(final_strategy))

# *************Strategies************* #

def bacon_strategy(score, opponent_score, margin=8, num_rolls=5):
    """This strategy rolls 0 dice if that gives at least MARGIN points,
    and rolls NUM_ROLLS otherwise.
    """
    opp_unit = opponent_score % 10
    opp_tens = (opponent_score - opp_unit) // 10

    if (abs(opp_tens - opp_unit) + 1) >= margin:
        return 0
    else:
        return num_rolls

def swap_strategy(score, opponent_score, margin=8, num_rolls=5):
    """This strategy rolls 0 dice when it would result in a beneficial swap and
    rolls NUM_ROLLS if it would result in a harmful swap. It also rolls
    0 dice if that gives at least MARGIN points and rolls
    NUM_ROLLS otherwise.
    """
    opp_unit = opponent_score % 10
    opp_tens = (opponent_score - opp_unit) // 10
    fb_points = abs(opp_tens - opp_unit) + 1 #Points to be gained by Free Bacon rule.

    if (score + fb_points) == (2 * opponent_score):
        return num_rolls
    if (score + fb_points) == (opponent_score / 2):
        return 0
    elif (fb_points) >= margin:
        return 0
    else:
        return num_rolls

def final_strategy(score, opponent_score):
    """I decided to rewrite the portions of the strategy that take advantage of
    the Free Bacon and Swine Swap rules, rather than call those strategies in
    this function, so that I would have flexibility in the ordering of conditional
    statements below.

    I improved my win rate through trial and error by changing the order and values
    in the conditional statements.

    I have tried to clarify the conditional statements with in-line comments
    below.
    """
    opp_unit = opponent_score % 10
    opp_tens = opponent_score // 10
    fb_points = abs(opp_tens - opp_unit) + 1 #Points to be gained by Free Bacon rule.
    fb_score = score + fb_points #Score if Free Bacon is used.

    if (fb_score) == (opponent_score / 2): #A beneficial Swap.
        return 0
    if (fb_score) == (opponent_score * 2): #Avoiding a detrimental Swap.
        return 4
    if (fb_points >= 9): #If Free Bacon gives a score of 9, take it.
        return 0

    if (score + opponent_score) % 7 == 0: #Vary rolls based on dice used.
        return 4
 
    if (score + 1 == opponent_score / 2): #Try to trigger a Swine Swap if behind by 1.
        return 10
    

    if opponent_score > 90 and score < 70: #A hail mary strategy if behind near the end of the game.
        return 8

    if score + opponent_score > 100: #Similar strategies as those described below, but for the endgame.
        if score *.85 > opponent_score:
            return 4
        if score * .95 > opponent_score:
            return 5

        if score * 1.25 < opponent_score:
            return 8
        if score * 1.1 < opponent_score:
            return 7
        else:
            return 6
    
    if score - 15 > opponent_score: #Less risk-taking when up by a larger amount.
        return 4
    if score - 5 > opponent_score: #Less risk-taking when up by a smaller amount.
        return 5

    if score + 25 < opponent_score: #More risk-taking when down by a larger amount.
        return 8
    if score + 10 < opponent_score: #More risk-taking when down by a smaller amount.
        return 7
    else:
        return 6 #If the scores are close, just roll 6 dice.


    
##########################
# Command Line Interface #
##########################

# Note: Functions in this section do not need to be changed.  They use features
#       of Python not yet covered in the course.


@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions.

    This function uses Python syntax/techniques not yet covered in this course.
    """
    import argparse
    parser = argparse.ArgumentParser(description="Play Hog")
    parser.add_argument('--run_experiments', '-r', action='store_true',
                        help='Runs strategy experiments')
    args = parser.parse_args()

    if args.run_experiments:
        run_experiments()
