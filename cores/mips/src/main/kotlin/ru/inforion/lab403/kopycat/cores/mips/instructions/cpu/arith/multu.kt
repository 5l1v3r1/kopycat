package ru.inforion.lab403.kopycat.cores.mips.instructions.cpu.arith

import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.kopycat.cores.mips.instructions.RdRsRtInsn
import ru.inforion.lab403.kopycat.cores.mips.operands.GPR
import ru.inforion.lab403.kopycat.modules.cores.MipsCore


/**
 * Created by a.gladkikh on 03/06/16.
 */
class multu(
        core: MipsCore,
        data: Long,
        rd: GPR,
        rs: GPR,
        rt: GPR) : RdRsRtInsn(core, data, Type.VOID, rd, rs, rt)  {

    override val mnem = "multu"

    override fun execute() {
        val v1 = vrs * vrt
        hi = v1[63..32]
        lo = v1[31..0]
    }
}