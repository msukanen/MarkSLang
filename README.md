# MarkSLang
Some sort of language(ish) thing to be.

# Features
Simple calculations, etc. Simple **REG**isters (for now only numeric).

# Commands
## PRINT
"Prints" given value or **REG** into output buffer.
* ``PRINT 2``
* ``PRINT A``
## MOV, ←
* ``MOV A 2``
* ``MOV B A``
* ``C ← 3``
* ``D ← E``
## ADD, +, SUB, -, MUL, *, DIV, /
Add, subtract, multiply, or divide given **REG** with something else. Result is stored in the given **REG**.
* ``ADD A 4``
* ``ADD B C``
* ``D + 5``
* ``E + F``
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
Reset all **REG** and restart the whole 'app'.
