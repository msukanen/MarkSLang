fun main() {
    val test_code = """
SET X 10
WHILE X is above zero, we'll run the following piece of code
  X - 1 happens to reduce the counter, right?
  R ← X and here the shrinking X is used as 'radius'.
  C ← π might have something to do with "PI", right?
  C * 2 ... multiplied
  C * R ... and then again, with radius this time.
  OUT C happens then to shovel the circumference into output buffer!
WEND then hops back to the WHILE X above.
NOP then out and what we have in
END oughta be like [56.548667764616276, 50.26548245743669,
                    43.982297150257104, 37.69911184307752,
                    31.41592653589793,  25.132741228718345,
                    18.84955592153876,  12.566370614359172,
                     6.283185307179586,  0.0]
    """
    val test_code2 = """
; Nothing surprising here, just checking if SQRT works...
  H ← 14  Opp f       ... by calculating the sine angle
  K ← 7  K ---- R     ... of A in this particular figure.    
  H / K    \   |
  A ← 3   r=14 | k=7
    √ A      \ |
  A / H       \A°
  OUT A
  END should result with [0.8660254037844386]
    """
    val test_code3 = """
; Simulate a sinusoidal signal with given sampling rate.
  IMP plot - yea, a plot twist - an IMPort from library!
  F ← 10Hz shouldn't be too high a frequency for this,
  O ← 30   but anyway, 30 is used as oversampling multi
  S ← O    to produce the final sampling freq...
  S * F    ...here.
  P ← 1    And then we calculate
  P / 3    phase shift ...
  P * π    ... in radians.
  N ← 5    5 cycles of sine wave seems a-ok?
  CALL sine_wave to sort it all then.
  OUT T
  OUT X
  CALL plot
  END
sine_wave:
  ; for future exercise, sort the math here!
  ; we'll use REGs F, S, P and N
  ; and results in T (time) & X (X-coord)
    """
    println( Executor(test_code2).run(true) )
}
