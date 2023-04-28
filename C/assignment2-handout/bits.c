/* 
 * CS:APP Data Lab 
 * 
 * <Asher Kirshtein ackirsht>
 * 
 * bits.c - Source file with your solutions to the Lab.
 *          This is the file you will hand in to your instructor.
 *
 * WARNING: Do not include the <stdio.h> header; it confuses the dlc
 * compiler. You can still use printf for debugging without including
 * <stdio.h>, although you might get a compiler warning. In general,
 * it's not good practice to ignore compiler warnings, but in this
 * case it's OK.  
 */

#if 0
/*
 * Instructions to Students:
 *
 * STEP 1: Read the following instructions carefully.
 */

You will provide your solution to the Data Lab by
editing the collection of functions in this source file.

INTEGER CODING RULES:
 
  Replace the "return" statement in each function with one
  or more lines of C code that implements the function. Your code 
  must conform to the following style:
 
  int Funct(arg1, arg2, ...) {
      /* brief description of how your implementation works */
      int var1 = Expr1;
      ...
      int varM = ExprM;

      varJ = ExprJ;
      ...
      varN = ExprN;
      return ExprR;
  }

  Each "Expr" is an expression using ONLY the following:
  1. Integer constants 0 through 255 (0xFF), inclusive. You are
      not allowed to use big constants such as 0xffffffff.
  2. Function arguments and local variables (no global variables).
  3. Unary integer operations ! ~
  4. Binary integer operations & ^ | + << >>
    
  Some of the problems restrict the set of allowed operators even further.
  Each "Expr" may consist of multiple operators. You are not restricted to
  one operator per line.

  You are expressly forbidden to:
  1. Use any control constructs such as if, do, while, for, switch, etc.
  2. Define or use any macros.
  3. Define any additional functions in this file.
  4. Call any functions.
  5. Use any other operations, such as &&, ||, -, or ?:
  6. Use any form of casting.
  7. Use any data type other than int.  This implies that you
     cannot use arrays, structs, or unions.

 
  You may assume that your machine:
  1. Uses 2s complement, 32-bit representations of integers.
  2. Performs right shifts arithmetically.
  3. Has unpredictable behavior when shifting an integer by more
     than the word size.

EXAMPLES OF ACCEPTABLE CODING STYLE:
  /*
   * pow2plus1 - returns 2^x + 1, where 0 <= x <= 31
   */
  int pow2plus1(int x) {
     /* exploit ability of shifts to compute powers of 2 */
     return (1 << x) + 1;
  }

  /*
   * pow2plus4 - returns 2^x + 4, where 0 <= x <= 31
   */
  int pow2plus4(int x) {
     /* exploit ability of shifts to compute powers of 2 */
     int result = (1 << x);
     result += 4;
     return result;
  }

FLOATING POINT CODING RULES

For the problems that require you to implent floating-point operations,
the coding rules are less strict.  You are allowed to use looping and
conditional control.  You are allowed to use both ints and unsigneds.
You can use arbitrary integer and unsigned constants.

You are expressly forbidden to:
  1. Define or use any macros.
  2. Define any additional functions in this file.
  3. Call any functions.
  4. Use any form of casting.
  5. Use any data type other than int or unsigned.  This means that you
     cannot use arrays, structs, or unions.
  6. Use any floating point data types, operations, or constants.


NOTES:
  1. Use the dlc (data lab checker) compiler (described in the handout) to 
     check the legality of your solutions.
  2. Each function has a maximum number of operators (! ~ & ^ | + << >>)
     that you are allowed to use for your implementation of the function. 
     The max operator count is checked by dlc. Note that '=' is not 
     counted; you may use as many of these as you want without penalty.
  3. Use the btest test harness to check your functions for correctness.
  4. Use the BDD checker to formally verify your functions
  5. The maximum number of ops for each function is given in the
     header comment for each function. If there are any inconsistencies 
     between the maximum ops in the writeup and in this file, consider
     this file the authoritative source.

/*
 * STEP 2: Modify the following functions according the coding rules.
 * 
 *   IMPORTANT. TO AVOID GRADING SURPRISES:
 *   1. Use the dlc compiler to check that your solutions conform
 *      to the coding rules.
 *   2. Use the BDD checker to formally verify that your solutions produce 
 *      the correct answers.
 */


#endif
/* Copyright (C) 1991-2018 Free Software Foundation, Inc.
   This file is part of the GNU C Library.

   The GNU C Library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   The GNU C Library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with the GNU C Library; if not, see
   <http://www.gnu.org/licenses/>.  */
/* This header is separate from features.h so that the compiler can
   include it implicitly at the start of every compilation.  It must
   not itself include <features.h> or any other header that includes
   <features.h> because the implicit include comes before any feature
   test macros that may be defined in a source file before it first
   explicitly includes a system header.  GCC knows the name of this
   header in order to preinclude it.  */
/* glibc's intent is to support the IEC 559 math functionality, real
   and complex.  If the GCC (4.9 and later) predefined macros
   specifying compiler intent are available, use them to determine
   whether the overall intent is to support these features; otherwise,
   presume an older compiler has intent to support these features and
   define these macros by default.  */
/* wchar_t uses Unicode 10.0.0.  Version 10.0 of the Unicode Standard is
   synchronized with ISO/IEC 10646:2017, fifth edition, plus
   the following additions from Amendment 1 to the fifth edition:
   - 56 emoji characters
   - 285 hentaigana
   - 3 additional Zanabazar Square characters */
/* We do not support C11 <threads.h>.  */
// Difficulty 1

/*
 * compareWithTmin - returns 1 if x is the minimum, two's complement number,
 *     and 0 otherwise 
 *   Legal ops: ! ~ & ^ | +
 *   Max ops: 10
 *   Rating: 1
 */

 /*
 *  if you do Tmin + Tmin you get 0 so we use the ! operator to change it to a 1
 *  if it isn't the min we just are left with a 0;
 *  We need the ^!x part for when x is zero. If it is zero we just put in all of the same bits 
 *  that only one has so we can be left with 0 since in that case we compare 1 to 1 and they have 
 *  all of the same bits so we are left with 0;
 */
int compareWithTmin(int x)
{
  int i = !(x+x) ^ !x;
  return i;
}
/* 
 * everyThirdBit - return word with every third bit (starting from the LSB) set to 1
 *   The only trick is to be economical with your operations
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 8
 *   Rating: 1
 */

 /*
 * We use 73 since in binary 73 = 01001001 we then shift it over 9 bytes so we can start at the LSB and have
 * alternating 1001. We could have used 4 or even 1 but 73 is more efficient. the | operand lets us keep the values that
 * were already in the original number; We then shift our number over 18 and take all of the same values again
 * we are left with our alteranting 1s.  
 */
int everyThirdBit(void)
{
  int x = (73 << 9) | 73;
  int i = (x << 18) | x;
  return i;
}
// Difficulty 2
/* 
 * setNewByteValue(x,n,c) - Replace the value of byte n in x with c
 *   Bytes numbered from 0 (least significant) to 3 (most significant)
 *   Examples: 
 *       setNewByteValue(0x12345678,1,0xab) = 0x1234ab78
 *       setNewByteValue(0x12345678,3,0xab) = 0xab345678
 *   You can assume 0 <= n <= 3 and 0 <= c <= 255
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 10
 *   Rating: 3
 *
 *
 * first we shift n over 3 bits
 * we then take the 255 since it is 0b11111111 and shift it over the amount of bits our i was
 * we'll then be left with either 0b11111111, 0b00111111, 0b00001111, or 0b00000011; 
 * We then shift our value we are inputting into the binary in the right spot. the same amount of 
 * shifts as we did before to match our 255 after the shifting. 
 * we then will compare all of the x and the flipped bits of our 255 value shifted 
 * we then fill the zeros that were left in when we flipped the bits by using the | operation
 * this leaves us with our values in the right place
 */
int setNewByteValue(int x, int n, int c)
{
  int i = n << 3;
  int j = 0xff << i;
  int k = c << i;
  int answer = (x & ~j) | k;
  return answer;
}
/* 
 * propagateLeastSignificantBit - set all bits of result to least significant bit of x
 *   Examples: 
 *        propagateLeastSignificantBit(5) --> 0xFFFFFFFF
 *        propagateLeastSignificantBit(6) --> 0x00000000
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 5
 *   Rating: 2
 *
 *
 * We start by getting the least significant bit of x by comparing it to 0x00000001
 * we then flip all of the bits and will be left with 0x11111110 and then we add 1 
 * and we are left with 0x11111111
 */
int propagateLeastSignificantBit(int x)
{
  int i = ~(x & 1);
  return i+1;
}
/* 
 * areAllOddBitsSetToOne - return 1 if all odd-numbered bits in x are set to 1,
 *   else return 0 
 *   Bits are counted right to left with the least-significant bit as bit 0, so the
 *   second-least-significant bit is the first odd bit.
 *   Examples: 
 *      areAllOddBitsSetToOne(0xFFFFFFFD) = 0, 
 *      areAllOddBitsSetToOne(0xAAAAAAAB) = 1
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 12
 *   Rating: 2
 *
 * We set i = 0b10101010. because we want to alternate bits
 * We then shift it over 8 bits and get our value which is more alternating bits
 * we then do that two more times so we have a value which is all alternating 1s and 0s
 * we then get all of the alternating values of x and then we use the ^(or) operation to 
 * get all of the same value across the binary number. if we have all 0s we just flip it to a
 * 1 with the ! operator. If it isn't zero then we had an odd bit and it becomes a 0;
 */
int areAllOddBitsSetToOne(int x) 
{
  int i = 0xAA; 
  i += (0xAA<<8); 
  i += (0xAA<<16); 
  i += (0xAA<<24);
  i = !((x&i) ^ i);
  return i;
}
// Difficulty 3
/* 
 * tripleOperator -  operatorsame as x ? y : z, the "triple" 
 *   Examples: 
 *      tripleOperator(10,4,5) --> 4
 *      tripleOperator(0,4,5)  --> 5
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 16
 *   Rating: 3
 *
 *
 * if x is 0 we want to set our variable equal to 0.
 * we then flip all of the bits in our i variable and get all of the ones that are shared with z
 * at the same time we get all of the shared bits of y and i and then compare the two. to leave us with 
 * so we know if either x or y is zero we return the value for z. if x or y is 0 then we want to return Z so we check that
 * by getting either -1 or 0 as our start. if its -1 the bits are filled so we can compare it to y and get y. when we do that
 * we will be flipping the bits to 0 and comparing it to z. or vise versa we are flipping all of the zero bits to fill them and
 * we are left with z and not y.
 */
int tripleOperator(int x, int y, int z)
{
 // int i = !x; 
  int a = (~i & z);
  int b = (i & y);
  int answer = a | b;
  return answer; 
}
/*
 * boundedDoubling - multiplies by 2, saturating to Tmin or Tmax if overflow
 * boundedDoubling - multiplies a signed number by 2, but do not allow overflow.
 *   If doubling a positive number is > Tmax, return Tmax.
 *   If doubling a negative number is < Tmin, return Tmin
 *   Examples: boundedDoubling(0x30000000) --> 0x60000000
 *             boundedDoubling(0x40000000) --> Tmax
 *             boundedDoubling(0xA0000000) --> TMin
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 20
 *   Rating: 3

 * We shift over 31 bits so if we had a positive or a negative number and fill the bit with it
 * we then shift over right one and then left 31 to fill a value with the most significant value
 * We then shift the bits of x doubling it 
 * We then check if i and j are equal by using the or operator if they aren't then there was an overflow and since 
 * there was an overflow we need to set the value to the max or the min. So we can compare and see if we have our max
 * we then use our ^ operator to make sure we didn't have the overflow and we then can return our value
 */
int boundedDoubling(int x) {
  int i = x >> 31; 
  int j = (x<<1) >> 31;  
  x <<= 1; 
  int h = (i^j);
  int l = (x^j^0x80000000);
  int k = h&l;
  x ^= k; 
  return x;
}
/* 
 * isGreaterThanZero - return 1 if x > 0, return 0 otherwise 
 *   Hint: you only need 3 distinct ops of the 8 listed
 *   Example: isGreaterThanZero(-1) = 0.
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 8
 *   Rating: 3
 *
 * first we check to see if the first bit is 1 or zero, either positive or negative
 * we then see if our value is 0 if it is 0 when we do the | statement we should be left with
 * 0 if we have an even number and if it is below zero wed have some number other than 0
 * we then use the ! operator to switch it and return it.
 * 
 */
int isGreaterThanZero(int x) {
  int a = (x&(1<<31));
  int b = a | !x;
  int i = !b;
  return i;
}
// Difficulty 4
/* 
 * float_timesFour - Return bit-level equivalent of expression 4*f for
 *   floating point argument f.
 *   Both the argument and result are passed as unsigned int's, but
 *   they are to be interpreted as the bit-level representation of
 *   single-precision floating point values.
 *   When argument is NaN, return argument
 *   Legal ops: Any integer/unsigned operations incl. ||, &&. also if, while
 *   Max ops: 34
 *   Rating: 4
 */
unsigned float_timesFour(unsigned uf) 
{ 
  
  unsigned i = (uf << 2); 
  return i; 
}
/*
 * countZeroBits - returns count of number of 1's in word
 *   Examples: countZeroBits(5) = 2, countZeroBits(7) = 3
 *   Legal ops: ! ~ & ^ | + << >>
 *   Max ops: 40
 *   Rating: 4
 *
 *  We first get a value i to = 0000001000000010000000100000001
 *  We then 
 */
int countZeroBits(int x) {
    int i = 1 | (1<<8) | (1<<16) | (1<<24); 
    int k = (x&i);
    k += ((x>>1)&i);
    k += ((x>>2)&i);
    k += ((x>>3)&i);
    k += ((x>>4)&i);
    k += ((x>>5)&i);
    k += ((x>>6)&i);
    k += ((x>>7)&i);
    int j = 0xff;
    int answer = (k&j) + ((k>>8)&j) + ((k>>16)&j) + ((k>>24)&j);
    return answer;
}
/*
 * timesPoint375 - multiply argument by 3/8 rounding toward 0,
 *  avoiding errors due to overflow
 *  Hint: You need a bias to round negative values properly
 *  Examples: timesPoint375(11) = 6
 *            timesPoint375(-9) = -3
 *            timesPoint375(0x30000000) = 0x12000000 (no overflow)
 *  Legal ops: ! ~ & ^ | + << >>
 *  Max ops: 25
 *  Rating: 4
 */
int timesPoint375(int x)
{
    return 2;
}
