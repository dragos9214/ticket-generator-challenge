# Ticket Generator Challenge

A small challenge that involves building a [Bingo 90](https://en.wikipedia.org/wiki/Bingo_(United_Kingdom)) ticket generator.

**Requirements:**

* Generate a strip of 6 tickets
  - Tickets are created as strips of 6, because this allows every number from 1 to 90 to appear across all 6 tickets. If they buy a full strip of six it means that players are guaranteed to mark off a number every time a number is called.
* A bingo ticket consists of 9 columns and 3 rows.
* Each ticket row contains five numbers and four blank spaces
* Each ticket column consists of one, two or three numbers and never three blanks.
  - The first column contains numbers from 1 to 9 (only nine),
  - The second column numbers from 10 to 19 (ten), the third, 20 to 29 and so on up until
  - The last column, which contains numbers from 80 to 90 (eleven).
* Numbers in the ticket columns are ordered from top to bottom (ASC).
* There can be **no duplicate** numbers between 1 and 90 **in the strip** (since you generate 6 tickets with 15 numbers each)

**Please make sure you add unit tests to verify the above conditions and an output to view the strips generated (command line is ok).**

Try to also think about the performance aspects of your solution. How long does it take to generate 10k strips? 
The recommended time is less than 1s (with a lightweight random implementation)

**Implementation:**

### Classes:
 - [Strip](src/main/java/com/lindar/bingo/generator/Strip.java) class consists of:
   - An ArrayList of 6 Ticket objects used for storing the tickets;
   - An integer id used for a cooler output.
 - [Ticket](src/main/java/com/lindar/bingo/generator/Ticket.java) class consists of:
   - A List of Lists storing the 3 ticket rows with 9 elements (the colum values) each; 
   - A List of integers keeping track of the total number of values allocated to the 3 rows; Used for distributing the values accordingly to the requirements.0 
   - A ticket position (0 to 5 including). It's used to store the ticket position within the strip.
 - [DistributedValueSet](src/main/java/com/lindar/bingo/generator/DistributedValueSet.java) class consists of:
   - A List of Lists of Lists: The first layer of lists represents the 6 tickets. Each of the 6 tickets consists of a list of 9 representing the columns of ticket. Each of the 9 column lists consists of up to 3 values representing row values of that column.
   - A List of Integers: Used for easy monitoring of the total values allocated to the 6 tickets.
 - [NumberFeed](src/main/java/com/lindar/bingo/generator/DistributedValueSet.java) class consists of:
   - A List of Lists: The list has 9 list elements in which the values from 1 to 90 are distributed acordignly to the rquirements 1-9, 10-19, ... , 80-90
 - [StripGenerator](src/main/java/com/lindar/bingo/generator/StripGenerator.java) class has no fields. 
 - [RandomUtil](src/main/java/com/lindar/bingo/generator/RandomUtil.java) class has no fields.
 - [Challenge](src/main/java/com/lindar/bingo/Challenge.java) class has no fields.
 
### Algorithm:

The algorithm consists of two main phases:
 - Distribution of values 1 to 90 randomly in a [DistributedValueSet](src/main/java/com/lindar/bingo/generator/DistributedValueSet.java) instance. A [DistributedValueSet](src/main/java/com/lindar/bingo/generator/DistributedValueSet.java) is created using the factory static method *generateRandomValueSet()*. The factory method creates an instance of [NumberFeed](src/main/java/com/lindar/bingo/generator/DistributedValueSet.java) used to ensure all 90 values are allocated to the DistributionValueSet and the resulting DistributionValueSet goes as follows:
   - At least one value is allocated randomly to every column of every ticket, resulting in 54 values out of 90 moved from the NumberFeed instance to the DistributionValueSet:
     - Column 1, range 1-9, 3 values remaining
     - Columns 2 to 8, ranges 10-19, 20-29 .. 70-79, 4 values remaining each
     - Column 9, range 80-90, 5 values remaining
   - Distribute randomly one value from the 5 remaining in range 80 - 90 to column 9 of a random ticket.
     - Column 9, range 80-90, 4 values remaining
   - Distribute 3 values per column from the remaining ones in the numberfeed to tickets sets with only one value per column.
     - Columns 2 to 9, 1 values remaining each
   - Distribute the remaining values to tickets with one or two values per column
     - At this point a corner case is treated: all tickets with column 9 (range 80 - 90) with less than 3 values are full overall (15 values). The solution is to find the emptiest ticket and its emptiest column, move a value from the target ticket there and finally fill column 8 with the value. 
   - Order the ticket colums ascending
 - Populate the [Ticket](src/main/java/com/lindar/bingo/generator/Ticket.java) with values from the random DistributedValueSet and blank spots. The [StripGenerator](src/main/java/com/lindar/bingo/generator/StripGenerator.java) class uses the above DistributedValueSet instance to generate the 6 ticket strip as follows:
   - Populates the first row with values from sets with 3 values, then with values from sets with 2 values and finally from 1 value sets until the row is full (5 values per ticket row).
   - Populates the second row with values from sets with 2 values, then with values from 1 value sets until the row is full (5 values per ticket row).
   - Populates the third row with values from sets with 1 value until the row is full (5 values per ticket row).
   
   Populating of any rows described above starts at a random index and increments with a random index in order to generate random ticket patterns.
   - The random increment is randomly selected from list 1, 2, 4, 5, 7, 8 in order to avoid setting only columns 0, 3 or 6 (corner case). 

   After all tickets were populated the algorithm make sure the tickets columns are sorted ascending.

### How to use:
The CLI is "minimalist", it expects a single argument, an integer greater than 0 representing the number of strips to be generated and printed.  
Running the program without arguments fallbacks to generating the default 10.000 strips.

### Tests:
The tests can be found [here](src/test/java/com/lindar/bingo/generator/StripGeneratorTest.java)

A set of 10.000 strips is generated before test execution. The time needed to generate the 10.000 strips is recorded in ms.
What tests cover:
 - Performance test: the time needs to generate 10.000 strips should be under 1000ms. ✔
 - Row distribution test: Each row has 5 numbers and 4 blanks. ✔
 - Column distribution test: No column has 3 blanks. ✔
 - Column order test: values in ticket are ascending. ✔
 - All 90 numbers appear test ✔
 - No duplicate number appears ✔
 - 72 blanks appear ✔
 - All 9 column ranges are respected ✔

***The above tests are validated against all 10.000 strips.***

### Other facts
There are two methods in the StripGenerator capable of generating strips:
 - *asyncGenerateMultiple(int count)* : generates the strips using a parallel stream
 - *syncGenerateMultiple(int count)* : generates the strips using a single stream 

 
The asynchronous method is used both in tests and at runtime.

Tests show that most runs of the synchronous method also check the performance constraint, 10.000 strips under 1000ms. 