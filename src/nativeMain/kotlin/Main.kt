fun main() {
    val test_code = """  X ← 10
  A ← 0
  WHILE X
    X - 1
    A + 1
    A * 1.5
  WEND
  PRINT A
"""
    println( Executor(test_code).run(true) )
}
