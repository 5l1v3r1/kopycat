package ru.inforion.lab403.kopycat.cores.mips.instructions.cpu.arith

import ru.inforion.lab403.common.extensions.asLong
import ru.inforion.lab403.kopycat.cores.mips.instructions.RtRsImmInsn
import ru.inforion.lab403.kopycat.cores.mips.operands.GPR
import ru.inforion.lab403.kopycat.cores.mips.operands.MipsImmediate
import ru.inforion.lab403.kopycat.modules.cores.MipsCore

/**
 * Created by a.gladkikh on 03/06/16.
 *
 * SLTIU rt, rs, immediate
 */
class sltiu(
        core: MipsCore,
        data: Long,
        rt: GPR,
        rs: GPR,
        imm: MipsImmediate) : RtRsImmInsn(core, data, Type.VOID, rt, rs, imm)  {

//    override val isSigned = false
    override val mnem = "sltiu"

    override fun execute() {
        vrt = (vrs < imm.usext).asLong
    }
}