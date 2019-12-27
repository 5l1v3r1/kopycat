package ru.inforion.lab403.kopycat.cores.arm.instructions.cpu.system

import ru.inforion.lab403.kopycat.cores.arm.enums.Condition
import ru.inforion.lab403.kopycat.cores.arm.instructions.AARMInstruction
import ru.inforion.lab403.kopycat.cores.base.exceptions.GeneralException
import ru.inforion.lab403.kopycat.cores.base.operands.Immediate
import ru.inforion.lab403.kopycat.modules.cores.AARMCore

/**
 * Created by a.gladkikh on 30.01.18
 */

class SVC(cpu: AARMCore,
          opcode: Long,
          cond: Condition,
          imm: Immediate<AARMCore>,
          size: Int):
        AARMInstruction(cpu, Type.VOID, cond, opcode, imm, size = size) {
    override val mnem = "SVC"

    override fun execute() {
        throw GeneralException("Not emplemented!")
    }
}