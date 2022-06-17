# MarkSLang
Some sort of language(ish) thing to be.

# Features
Simple calculations, loops, conditional(s), etc. Simple **REG**isters (for now only numeric).

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
End program.
## NOP
No-operation. May or may not be of some use for someone...
## SWAP, ↔
Swap contents of two **REG**.
* ``SWAP A B``
* ``C ↔ D``
* ``F <-> E``
## CALL, RET
Call a / return from sub-routine.
* ``CALL some_sub_somewhere``
* ``RET``

Note that if **CALL** stack is empty **RET** will act as **NOP** and the next command in pipeline will be executed. Occasionally useful, but potentially hazardous, too... ;-)
## TRIM
Trims away decimals in **REG**.
* ``TRIM A``
## SUM
Sum into **REG** two other **REG**/**DTA**.
* ``SUM C 3 4`` - sum ``3`` and ``4`` into **REG**``C``
* ``SUM C A B`` - sum ``A`` and ``B`` into **REG**``C``
* ``SUM C A A`` - sum ``A`` twice into **REG**``C`` 
## RESET
Reset all **REG**.
## RESTART
Restart app from scratch.
## WHILE, WEND
Loop...
```
X ← 10
A ← 0
WHILE X
  X - 1
  A + 1
  A * 1.5
WEND
OUT A
```
```
[169.9951171875]
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