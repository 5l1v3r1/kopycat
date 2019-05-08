package ru.inforion.lab403.kopycat.cores.mips.instructions.cpu.logical

import ru.inforion.lab403.kopycat.cores.mips.instructions.RdRsRtInsn
import ru.inforion.lab403.kopycat.cores.mips.operands.GPR
import ru.inforion.lab403.kopycat.modules.cores.MipsCore

/**
 * Created by batman on 03/06/16.
 *
 * OR rd, rs, rt
 *
 * To do a boolbitman logical OR
 *
 * The contents of GPR rs are combined with the contents of GPR rt in a boolbitman logical OR operation.
 * The result is placed into GPR rd.
 */
class orr(
        core: MipsCore,
        data: Long,
        rd: GPR,
        rs: GPR,
        rt: GPR) : RdRsRtInsn(core, data, Type.VOID, rd, rs, rt) {

    override val mnem = "or"

    override fun execute() {
        vrd = vrs or vrt
    }

}