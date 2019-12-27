package ru.inforion.lab403.kopycat.modules.debuggers

import ru.inforion.lab403.common.extensions.get
import ru.inforion.lab403.kopycat.cores.base.common.Debugger
import ru.inforion.lab403.kopycat.cores.base.common.Module
import ru.inforion.lab403.kopycat.modules.cores.x86Core


/**
 * Created by a.gladkikh on 05/06/16.
 */

class x86Debugger(parent: Module, name: String): Debugger(parent, name) {
    override fun ident() = "i386"

    override fun registers(): MutableList<Long> = Array(32) { regRead(it) }.toMutableList()

    override fun regRead(index: Int): Long {
        val core = core as x86Core
        return when (index) {
            0x00 -> core.cpu.regs.eax
            0x01 -> core.cpu.regs.ecx
            0x02 -> core.cpu.regs.edx
            0x03 -> core.cpu.regs.ebx
            0x04 -> core.cpu.regs.esp
            0x05 -> core.cpu.regs.ebp
            0x06 -> core.cpu.regs.esi
            0x07 -> core.cpu.regs.edi
            0x08 -> core.cpu.pc
            0x09 -> core.cpu.flags.eflags
            0x0A -> core.cpu.sregs.cs[19..0]
            0x0B -> core.cpu.sregs.ss[19..0]
            0x0C -> core.cpu.sregs.ds[19..0]
            0x0D -> core.cpu.sregs.es[19..0]
            0x0E -> core.cpu.sregs.fs[19..0]
            0x0F -> core.cpu.sregs.gs[19..0]
            0x10 -> core.fpu[0]
            0x11 -> core.fpu[1]
            0x12 -> core.fpu[2]
            0x13 -> core.fpu[3]
            0x14 -> core.fpu[4]
            0x15 -> core.fpu[5]
            0x16 -> core.fpu[6]
            0x17 -> core.fpu[7]
            0x18 -> core.fpu.fwr.FPUControlWord
            0x19 -> core.fpu.fwr.FPUStatusWord
            0x1A -> core.fpu.fwr.FPUTagWord
            0x1B -> core.fpu.fwr.FPUInstructionPointer
            0x1C -> 0
            0x1D -> core.fpu.fwr.FPUDataPointer
            0x1E -> 0
            0x1F -> core.fpu.fwr.FPULastInstructionOpcode
            else -> 0xDEADBEEF
        }
    }

    override fun regWrite(index: Int, value: Long) {
        val core = core as x86Core
//        log.fine { "Write register $index to %08X".format(value) }
        when (index) {
            0x00 -> core.cpu.regs.eax = value
            0x01 -> core.cpu.regs.ecx = value
            0x02 -> core.cpu.regs.edx = value
            0x03 -> core.cpu.regs.ebx = value
            0x04 -> core.cpu.regs.esp = value
            0x05 -> core.cpu.regs.ebp = value
            0x06 -> core.cpu.regs.esi = value
            0x07 -> core.cpu.regs.edi = value
            0x08 -> {
                log.warning { "Setup EIP register, this action works only within one segment..." }
                log.warning { "IDA Pro probably show you a bloody mess ... don't worry it's ok just go ahead!" }
                core.cpu.pc = value
                if (core.cpu.exception != null) {  // Deleting hardware exception when changing PC
                    core.cpu.exception = null
                    log.warning { "Hardware exception was cleared!" }
                }
            }
            0x09 -> core.cpu.flags.eflags = value
            0x0A -> core.cpu.sregs.cs = value
            0x0B -> core.cpu.sregs.ss = value
            0x0C -> core.cpu.sregs.ds = value
            0x0D -> core.cpu.sregs.es = value
            0x0E -> core.cpu.sregs.fs = value
            0x0F -> core.cpu.sregs.gs = value
            0x10 -> core.fpu[0] = value
            0x11 -> core.fpu[1] = value
            0x12 -> core.fpu[2] = value
            0x13 -> core.fpu[3] = value
            0x14 -> core.fpu[4] = value
            0x15 -> core.fpu[5] = value
            0x16 -> core.fpu[6] = value
            0x17 -> core.fpu[7] = value
            0x18 -> core.fpu.fwr.FPUControlWord = value
            0x19 -> core.fpu.fwr.FPUStatusWord = value
            0x1A -> core.fpu.fwr.FPUTagWord = value
            0x1B -> core.fpu.fwr.FPUInstructionPointer = value
            0x1D -> core.fpu.fwr.FPUDataPointer = value
            0x1F -> core.fpu.fwr.FPULastInstructionOpcode = value
            else -> {

            }
        }
    }
}