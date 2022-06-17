# MarkSLang
**M***ark***SL***ang* Ⓒ 2022 *Markku Sukanen*. 
Some sort of language(ish) thing to be.

# Features
Simple calculations, loops, conditional(s), etc. Simple **REG**isters (for now only numeric).

## What's What?
* **REG** refers to so-called "registers", which range from **A** to **Z**.
* **DTA** refers to some raw data value, e.g. ``12.075``, ``404``, etc.
* **LOC** in source code designates a "jump point" (or just a convenient tag), e.g. ``this_is_a_loc:``
* **CMD** refers to any context-valid command.
* **CMP** refers to one of the many compare/compute things: ``<``, ``>``, ``≠``, ``≤``, ``&`` etc.

## Code Commenting
```
; any line beginning with ';' is treated as a comment and thus ignored.
NOP and just the same is everything ignored after 'NOP' "command".
  ; align
    ; doesn't
       ; matter
```
There's also the fact that all commands ignore any and everything that
follows the stuff they munch, and thus something like...
```
WHILE X is above zero, we'll run the following piece of code
  X - 1 happens to reduce the counter, right?
  R ← X and here the shrinking X is used as 'radius'.
  C ← 3.141 might have something to do with "PI", right?
  C * 2 ... multiplied
  C * R ... and then again, with radius this time.
  OUT C happens then to shovel the circumference into output buffer!
WEND then hops back to the WHILE X above.
```
... is a completely fine way to prettify / complicate your code!
# Commands
## OUT
"Prints" given value or **REG** into output buffer.
* ``OUT 2``
* ``OUT A``
## SET, ←
Set a **REG**'s value.
* ``SET A 2`` - set ``A`` to contain ``2``
* ``SET B A`` - set ``B`` to contain whatever is in ``A``
* ``C ← 3``
* ``D ← E``
## ADD, +, SUB, -, MUL, *, DIV, /
Add, subtract, multiply, or divide given **REG** with something else. Result is stored in the given **REG**.
* ``A + 4`` ... ``ADD A 4``
* ``B - C`` ... ``SUB B C``
* ``D * 5`` ... ``MUL D 5``
* ``E / F`` ... ``DIV E F``
## JUMP, →
Jump somewhere else in the code.
* ``JUMP somewhere_else``
* ``somewhere_else →``
## IF
If something is something, then execute a **CMD**.

**IF &lt;reg/dta&gt; &lt;cmp&gt; &lt;reg/dta&gt; &lt;cmd&gt; &lt;loc&gt;**
* ``IF A < B JUMP only_if_a_was_lt_b`` - jump to *only_if_a_was_lt_b* if ``A``is less than ``B``. Note that whitespace between **REG/DTA**, **CMP** and 2nd **REG/DTA** is ***significant!*** 
## END
The End. End program.

Program naturally ends when it runs of code to execute,
but ``END`` can be used to quit it at some arbitrary point in middle of
a chunk of code.
## NOP
No-operation. May or may not be of some use for someone...
Any and everything following ``NOP`` is ignored, and thus
``NOP`` can be used for code commenting,
```
here_be_code:
  NOP A pointless set operation follows, for demonstration purposes...
  NOP ...or just to state that 42 is the answer to L.
  SET L 42
  END
```
## SWAP, ↔
Swap contents of two **REG**.
* ``SWAP A B``
* ``C ↔ D``
* ``F <-> E``
## CALL, RET
Call a / return from sub-routine.
* ``CALL some_sub_somewhere``
* ``RET``

Note that if **CALL** stack is empty then **RET** will act
as **NOP** and execution of program will "fall through".
Occasionally useful, yet potentially hazardous **;-)**
## TRIM
Chop off all decimals from **REG**. This ye olde float-to-int truncation.
* ``TRIM A``
## SUM
Sum into **REG** two other **REG**/**DTA**.
* ``SUM C 3 4`` - sum ``3`` and ``4`` into **REG**``C``
* ``SUM C A B`` - sum ``A`` and ``B`` into **REG**``C``
* ``SUM C A A`` - sum ``A`` twice into **REG**``C`` 
## RESET
Reset all **REG**.
## RESTART
Restart app from scratch. At this point in time not very useful,
but who knows?
## WHILE, WEND
A rather basic looping operation.
```
X ← 10
A ← 0
WHILE X
  ; while X>0, keep looping
  X - 1
  A + 1
  A * 1.5
WEND
OUT A
```
```
[169.9951171875]
```
## PI, π
"PI" or π is what one might expect it to be, approx. 3.1415926...
* ``PI E`` sets **REG**``E`` to hold PI.
* ``π E``
* ``ADD C PI`` adds PI to whatever **REG**``C`` contains.
* ``R * π`` multiplies **REG**``R`` with PI.

## ABS
Abs a **REG**. In other words, drop any notion of negative value.
```
SET X -10
ABS X
; results with X having 10 in it.
```

# Example Code
Shovel prime numbers below 50 into output buffer: 
```
  N ← 50
  OUT 2
  A ← 3
start:
  B ← 2
  Z ← 0
test:
  C ← B
new:
  IF C = A JUMP err
  IF C > A JUMP past1
  C + B
  JUMP new
err:
  Z ← 1
  JUMP past2
past1:
  B + 1
  IF B < A JUMP test
past2:
  IF Z = 1 JUMP past3
  OUT A
past3:
  A + 1
  IF A ≤ N JUMP start
```