package ru.inforion.lab403.kopycat.cores.mips.instructions.cop.branch

import ru.inforion.lab403.kopycat.cores.base.exceptions.GeneralException
import ru.inforion.lab403.kopycat.cores.mips.instructions.CcOffsetInsn
import ru.inforion.lab403.kopycat.cores.mips.operands.MipsImmediate
import ru.inforion.lab403.kopycat.cores.mips.operands.MipsNear
import ru.inforion.lab403.kopycat.modules.cores.MipsCore


/**
 * Created by a.gladkikh on 03/06/16.
 *
 * BC2TL cc, offset
 */
class bc2tl(
        core: MipsCore,
        data: Long,
        cc: MipsImmediate,
        off: MipsNear) : CcOffsetInsn(core, data, Type.VOID, cc, off) {

    override val mnem = "bc2tl"

    override fun execute() {
        throw GeneralException("Sorry, but I don't know how to execute this instruction!")
    }
}

