import kotlin.random.Random

/**
 * Executes everything unimaginable.
 *
 * @param src_code MarkSLang source code string/chunk/something.
 */
class Executor(private val src_code: String) {
    /**
     * Parameter types for commands.
     */
    enum class PType {
        REG, DTA, D_R, CMD, CMP, LOC,
    }

    /**
     * Comparison and computation types for e.g. IF.
     */
    enum class CMPType {
        LT, EQ, GT, LT_EQ, GT_EQ, NEQ, AND,
    }

    /**
     * Interface for all sorts of Cmd* grouping.
     */
    interface CmdParamI

    /**
     * Cmd parameters should chew on this.
     *
     * @param types Param types, in desired (specific) order.
     */
    data class CmdParam(val types:List<PType>) : CmdParamI {
        constructor() : this(listOf())
    }

    /**
     * For aliasing something else with a root command of some sort.
     *
     * @param with Cmd 'X' will be swapped with this one whenever encountered.
     */
    data class CmdSwap(val with:String) : CmdParamI

    /**
     * Handle JUMP, WHILE, etc. jump points.
     *
     * @param src Source code, line by line.
     */
    private fun compile(src:List<String>): Pair<Map<String, Int>, List<List<String>>> {
        val raw = src.toMutableList()
        val jmpPoints = mutableMapOf<String, Int>()
        val repl1 = ArrayDeque<Pair<String, String>>()
        val repl2 = mutableListOf<Pair<Int, String>>()
        var whereTo: String
        var prc = 0
        raw.forEach {
            // lets extract jump points 1st round
            val ln = it.split(Regex("\\s+")).toMutableList()
            if (Regex("^(__wend__)?[a-z_\\d]+:?\\s*$").matches(ln[0]))
                jmpPoints[ln[0].replace(":", "")] = prc
            else if (ln[0] == "WHILE") {
                // TODO should be UUID level of randomness used, but this'll do for now...
                var num = Random.nextLong()
                if (num < 0L)
                    num = -num
                whereTo = num.toString()
                val addr = "__wend__${ln[1].lowercase()}${whereTo}"
                repl1.addLast(Pair(ln[1], addr))
                jmpPoints[addr] = prc
            } else if (ln[0] == "WEND") {
                val addr = repl1.removeLast()
                repl2.add(Pair(prc, "IF ${addr.first} > 0 JUMP ${addr.second}"))
            }
            prc++
        }
        jmpPoints.forEach { raw[it.value] = "NOP" }
        repl2.forEach { raw[it.first] = it.second }
        val compiled = mutableListOf<List<String>>()
        raw.forEach {
            // validate all commands and stuff
            val ln = it.split(Regex("\\s+")).toMutableList()

            if (ln.size > 1 && ln[1] in known_mid_ops) {
                val t = ln[0]
                ln[0] = ln[1]
                ln[1] = t
            }

            if (ln[0] in known_cmds.keys) {
                var cmd = known_cmds[ln[0]]
                if (cmd is CmdSwap) {
                    ln[0] = cmd.with
                    cmd = known_cmds[cmd.with]
                }

                compiled.add(ln)

                ln.forEachIndexed { index, s ->
                    if (index > 0 && !when ((cmd as CmdParam).types[index - 1]) {
                            PType.REG -> isREG(s)
                            PType.DTA -> isDTA(s)
                            PType.LOC -> isLOC(s)
                            PType.CMD -> isCMD(s)
                            PType.CMP -> isCMP(s)
                            PType.D_R -> isDTA(s) || isREG(s)
                        }
                    ) throw IllegalStateException("In \"$it\": '$s' is not ${cmd.types[index - 1]}")
                }
            }
        }

        return Pair(jmpPoints.toMap(), compiled.toList())
    }

    /**
     * Time to run the fun!
     *
     * @param verbose Be verbose?
     */
    fun run(verbose:Boolean = false): List<String> {
        val (jmpPoints, compiled) = compile(src_code.trim().split(regex = Regex("\\s*\n\\s*")))
        val vars = mutableMapOf<String, Number>()
        resetVars(vars)
        val out = mutableListOf<String>()
        val stack = ArrayDeque<Int>()
        var prc = 0
        while (prc < compiled.size) {
            val cmd = compiled[prc]
            if (verbose) println(cmd)
            prc++
            when(cmd[0]) {
                "NOP" -> continue
                "PRINT" -> out.add(varOrVal(vars, cmd[1]).toString())
                "MOV" -> vars[cmd[1]] = varOrVal(vars, cmd[2])
                "ADD" -> vars[cmd[1]] = funcAdd(vars[cmd[1]]!!, varOrVal(vars, cmd[2]))
                "SUB" -> vars[cmd[1]] = funcSub(vars[cmd[1]]!!, varOrVal(vars, cmd[2]))
                "MUL" -> vars[cmd[1]] = funcMul(vars[cmd[1]]!!, varOrVal(vars, cmd[2]))
                "DIV" -> vars[cmd[1]] = funcDiv(vars[cmd[1]]!!, varOrVal(vars, cmd[2]))
                "JUMP" -> prc = jmpPoints[cmd[1]]!!
                "END" -> break
                "IF" -> if (cmp(varOrVal(vars, cmd[1]), known_cmp[cmd[2]]!!, varOrVal(vars, cmd[3]))) {
                    prc = jmpPoints[cmd[5]]!!
                }
                "SWAP" -> {
                    val t = vars[cmd[1]]!!
                    vars[cmd[1]] = vars[cmd[2]]!!
                    vars[cmd[2]] = t
                }
                "CALL" -> {
                    stack.addLast(prc)
                    prc = jmpPoints[cmd[1]]!!
                }
                "RET" -> {
                    // RET is intentionally doing *nothing* if stack is empty...
                    // with possibly odd (or actually desired) results in such a case.
                    val goto = stack.removeLastOrNull()
                    if (goto != null)
                        prc = goto
                }
                "TRIM" -> vars[cmd[1]] = vars[cmd[1]]!!.toInt()
                "SUM" -> vars[cmd[1]] = funcAdd(varOrVal(vars, cmd[2]), varOrVal(vars, cmd[3]))
                "RESET" -> resetVars(vars)
            }
        }
        return out.toList()
    }

    companion object {
        /**
         * Handle addition.
         *
         * @param a Some Number.
         * @param b Some Number.
         * @return a Number, obviously.
         */
        inline fun <reified T : Number, reified U : Number> funcAdd(a: T, b: U): Number {
            return when {
                a is Int && b is Int -> a + b
                else -> a.toDouble() + b.toDouble()
            }
        }
        /**
         * Handle subtraction.
         *
         * @param a Some Number.
         * @param b Some Number.
         * @return a Number, obviously.
         */
        inline fun <reified T : Number, reified U : Number> funcSub(a: T, b: U): Number {
            return when {
                a is Int && b is Int -> a - b
                else -> a.toDouble() - b.toDouble()
            }
        }
        /**
         * Handle multiplication.
         *
         * @param a Some Number.
         * @param b Some Number.
         * @return a Number, obviously.
         */
        inline fun <reified T : Number, reified U : Number> funcMul(a: T, b: U): Number {
            return when {
                a is Int && b is Int -> a * b
                else -> a.toDouble() * b.toDouble()
            }
        }
        /**
         * Handle division.
         *
         * @param a Some Number.
         * @param b Some Number.
         * @return a Number, obviously.
         */
        inline fun <reified T : Number, reified U : Number> funcDiv(a: T, b: U): Number = a.toDouble() / b.toDouble()

        /**
         * Param is REG? FYI: REG is a singular uppercase letter...
         */
        private fun isREG(s:String): Boolean = ("A".."Z").contains(s)

        /**
         * Param is DTA? FYI/TODO: DTA is a numeric type of some sort, most often Int.
         */
        private fun isDTA(s:String): Boolean = Regex("^\\d+$").matches(s)

        /**
         * Param is LOC? FYI: LOG is in source code a jump point designator, e.g. 'jump_here:'.
         */
        private fun isLOC(s:String): Boolean = Regex("^(__wend__)?[a-z_\\d]+$").matches(s)

        /**
         * Param is a CMD (e.g. 'JUMP')?
         */
        private fun isCMD(s:String): Boolean = s == "JUMP"

        /**
         * Param is a CMP? FYI: CMP is a compare/computation operator of some sort.
         */
        private fun isCMP(s:String): Boolean = s in known_cmp

        /**
         * Unify DTA/REG for 'read access' consumption.
         *
         * @param v Vars to fetch REG from.
         * @param s Something what's either DTA or REG.
         * @return TODO: a Number until we support other data types.
         */
        private fun varOrVal(v:Map<String, Number>, s:String): Number {
            if (s in v)
                return v[s]!!
            if (s.contains("."))
                return s.toDouble()
            return s.toInt()
        }

        /**
         * Compare/compute something.
         *
         * @param v1 Some Number.
         * @param v2 Some Number.
         * @param CMP CoMPuter to use for v1/v2.
         */
        private fun cmp(v1:Number, CMP:CMPType, v2:Number): Boolean = when(CMP) {
            CMPType.EQ -> v1 == v2
            CMPType.NEQ -> v1 != v2
            CMPType.LT -> v1.toDouble() < v2.toDouble()
            CMPType.GT -> v1.toDouble() > v2.toDouble()
            CMPType.LT_EQ -> v1.toDouble() <= v2.toDouble()
            CMPType.GT_EQ -> v1.toDouble() >= v2.toDouble()
            CMPType.AND -> when {
                v1 is Int && v2 is Int -> (v1 and v2) != 0
                else -> false
            }
        }

        /**
         * Reset all vars in given var-map.
         *
         * @param v Variable map to reset.
         */
        private fun resetVars(v:MutableMap<String, Number>) {
            for (i in 'A'..'Z')
                v[i.toString()] = 0
        }

        /**
         * Map of recognized commands and their parameter types.
         */
        private val known_cmds = mapOf(
            "PRINT" to CmdParam(listOf(PType.D_R)),
            "MOV" to CmdParam(listOf(PType.REG, PType.D_R)),
            "ADD" to CmdParam(listOf(PType.REG, PType.D_R)),
            "SUB" to CmdParam(listOf(PType.REG, PType.D_R)),
            "MUL" to CmdParam(listOf(PType.REG, PType.D_R)),
            "JUMP" to CmdParam(listOf(PType.LOC)),
            "IF" to CmdParam(listOf(PType.D_R, PType.CMP, PType.D_R, PType.CMD, PType.LOC)),
            "END" to CmdParam(),
            // v2 -- extending on v1
            "NOP" to CmdParam(),
            "SWAP" to CmdParam(listOf(PType.REG, PType.REG)),
            "CALL" to CmdParam(listOf(PType.LOC)),
            "RET" to CmdParam(),
            "↔" to CmdSwap("SWAP"),
            "<->" to CmdSwap("SWAP"),
            "←" to CmdSwap("MOV"),
            "+" to CmdSwap("ADD"),
            "-" to CmdSwap("SUB"),
            "→" to CmdSwap("JUMP"),
            "DIV" to CmdParam(listOf(PType.REG, PType.D_R)),
            "TRIM" to CmdParam(listOf(PType.REG)),
            "SUM" to CmdParam(listOf(PType.REG, PType.D_R, PType.D_R)),
            "RESET" to CmdParam(),
        )

        /**
         * List of infix/postfix operators/commands.
         */
        private val known_mid_ops = listOf("↔", "<->", "←", "+", "-", "→")

        /**
         * Map of CMP operations.
         */
        private val known_cmp = mapOf(
            "=" to CMPType.EQ, "==" to CMPType.EQ,
            "!=" to CMPType.NEQ, "≠" to CMPType.NEQ, "<>" to CMPType.NEQ,
            "<" to CMPType.LT, "≤" to CMPType.LT_EQ, "<=" to CMPType.LT_EQ,
            ">" to CMPType.GT, "≥" to CMPType.GT_EQ, ">=" to CMPType.GT_EQ,
            "&" to CMPType.AND,
        )
    }
}
