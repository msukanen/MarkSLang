fun main() {
    val test_code = """
        N ← 50
        PRINT 2
        A ← 3
    alku:
        B ← 2
        Z ← 0
    testi:
        C ← B
    uusi:
        IF C = A JUMP virhe
        IF C > A JUMP ohi
        C + B
        JUMP uusi
    virhe:
        Z ← 1
        JUMP ohi2
    ohi:
        B + 1
        IF B < A JUMP testi
    ohi2:
        IF Z = 1 JUMP ohi3
        PRINT A
    ohi3:
        A + 1
        IF A ≤ N JUMP alku
        A ← 10
        WHILE A
        A - 1
        PRINT A
        B ← 3
        C ← 5
        WHILE B
        PRINT C
        PRINT B
        B - 1
        WEND
        WEND
    """
    println( Executor(test_code).run(true) )
}
