fun main() {
    val test_code = """
SET X 10
WHILE X is above zero, we'll run the following piece of code
  X - 1 happens to reduce the counter, right?
  R ← X and here the shrinking X is used as 'radius'.
  C ← 3.141 might have something to do with "PI", right?
  C * 2 ... multiplied
  C * R ... and then again, with radius this time.
  OUT C happens then to shovel the circumference into output buffer!
WEND then hops back to the WHILE X above.
    """
    println( Executor(test_code).run(true) )
}
