package ru.inforion.lab403.kopycat.cores.arm.instructions.cpu.arithm.immediate

import ru.inforion.lab403.kopycat.cores.arm.AddWithCarry
import ru.inforion.lab403.kopycat.cores.arm.enums.Condition
import ru.inforion.lab403.kopycat.cores.arm.exceptions.ARMHardwareException.Unpredictable
import ru.inforion.lab403.kopycat.cores.arm.hardware.flags.FlagProcessor
import ru.inforion.lab403.kopycat.cores.arm.instructions.AARMInstruction
import ru.inforion.lab403.kopycat.cores.arm.operands.ARMRegister
import ru.inforion.lab403.kopycat.cores.base.operands.Immediate
import ru.inforion.lab403.kopycat.modules.cores.AARMCore



class SUBi(cpu: AARMCore,
           opcode: Long,
           cond: Condition,
           var setFlags: Boolean,
           var rd: ARMRegister,
           var rn: ARMRegister,
           var imm32: Immediate<AARMCore>,
           size: Int):
        AARMInstruction(cpu, Type.VOID, cond, opcode, rd, rn, imm32, size = size) {

    override val mnem = "SUB${if(setFlags) "S" else ""}$mcnd"

    override fun execute() {
        val (result, carry, overflow) = AddWithCarry(rn.dtyp.bits, rn.value(core), imm32.value.inv(), 1)
        if(rd.reg == core.cpu.regs.pc.reg) {
            if(setFlags) throw Unpredictable
            core.cpu.ALUWritePC(result)
        } else {
            rd.value(core, result)
            if (setFlags)
                FlagProcessor.processArithmFlag(core, result, carry, overflow)
        }
    }
}