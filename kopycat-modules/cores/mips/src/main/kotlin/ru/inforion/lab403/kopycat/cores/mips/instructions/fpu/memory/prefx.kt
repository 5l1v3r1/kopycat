package ru.inforion.lab403.kopycat.cores.mips.instructions.fpu.memory

import ru.inforion.lab403.kopycat.cores.base.exceptions.GeneralException
import ru.inforion.lab403.kopycat.cores.mips.instructions.OpOffsetBaseInsn
import ru.inforion.lab403.kopycat.cores.mips.operands.MipsDisplacement
import ru.inforion.lab403.kopycat.cores.mips.operands.MipsImmediate
import ru.inforion.lab403.kopycat.modules.cores.MipsCore

/**
 *
 * PREFX hint, index(base)
 */
class prefx(
        core: MipsCore,
        data: Long,
        imm: MipsImmediate,
        off: MipsDisplacement
) : OpOffsetBaseInsn(core, data, Type.VOID, imm, off) {

    override val mnem = "prefx"

    override fun execute() {
        throw GeneralException("Sorry, but I don't know how to execute this instruction!")
    }
}