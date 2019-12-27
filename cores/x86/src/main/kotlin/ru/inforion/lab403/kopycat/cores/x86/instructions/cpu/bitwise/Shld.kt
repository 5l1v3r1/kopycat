package ru.inforion.lab403.kopycat.cores.x86.instructions.cpu.bitwise

import ru.inforion.lab403.common.extensions.asInt
import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.common.extensions.mask
import ru.inforion.lab403.kopycat.cores.base.exceptions.GeneralException
import ru.inforion.lab403.kopycat.cores.base.operands.AOperand
import ru.inforion.lab403.kopycat.cores.base.operands.Variable
import ru.inforion.lab403.kopycat.cores.x86.hardware.flags.FlagProcessor
import ru.inforion.lab403.kopycat.cores.x86.hardware.systemdc.Prefixes
import ru.inforion.lab403.kopycat.cores.x86.instructions.AX86Instruction
import ru.inforion.lab403.kopycat.modules.cores.x86Core

/**
 * Created by a.gladkikh on 21.06.18.
 */

class Shld(core: x86Core, opcode: ByteArray, prefs: Prefixes, vararg operands: AOperand<x86Core>):
        AX86Instruction(core, Type.VOID, opcode, prefs, *operands) {
    override val mnem = "shld"

    override val cfChg = true
    override val ofChg = true
    override val afChg = true
    override val zfChg = true
    override val sfChg = true

    override fun execute() {
        var dst = op1.value(core)
        val src = op2.value(core)
        val operandSize = op1.dtyp.bits
        val count = op3.value(core).asInt % 32
        if (count != 0) {
            if (count < operandSize) {
                val cf = dst[operandSize - count] == 1L

                val msb2 = operandSize - 1
                val lsb2 = operandSize - count

                dst = (dst shl count) or src[msb2..lsb2]

                val result = Variable<x86Core>(dst, op1.dtyp)
                FlagProcessor.processShiftFlag(core, result, op1, op2, true, false, cf)
                op1.value(core, result)
            } else {
                throw GeneralException("count ($count) >= operand size ($operandSize) for shld")
            }
        }
    }
}