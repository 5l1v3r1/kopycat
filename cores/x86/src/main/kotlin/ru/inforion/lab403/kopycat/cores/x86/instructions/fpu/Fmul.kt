package ru.inforion.lab403.kopycat.cores.x86.instructions.fpu

import ru.inforion.lab403.common.extensions.ieee754
import ru.inforion.lab403.kopycat.cores.base.operands.AOperand
import ru.inforion.lab403.kopycat.cores.x86.exceptions.x86HardwareException
import ru.inforion.lab403.kopycat.cores.x86.hardware.systemdc.Prefixes
import ru.inforion.lab403.kopycat.cores.x86.instructions.AX86Instruction
import ru.inforion.lab403.kopycat.modules.cores.x86Core
import kotlin.math.absoluteValue

/**
 * Created by v.davydov on 08.09.16.
 */

class Fmul(core: x86Core, opcode: ByteArray, prefs: Prefixes, val popCount: Int, vararg operands: AOperand<x86Core>):
        AX86Instruction(core, Type.VOID, opcode, prefs, *operands) {
    override val mnem = "fmul"

    override fun execute() {
        val a1 = op1.value(core).ieee754()
        val a2 = op2.value(core).ieee754()
        if((a1 == 0.0 && a2.absoluteValue == Double.MAX_VALUE) || (a2 == 0.0 && a1.absoluteValue == Double.MAX_VALUE)) {
            throw x86HardwareException.FpuException(core.pc)
        }
        val res = a1 * a2
        op1.value(core, res.ieee754())
        core.fpu.pop(popCount)
    }
}