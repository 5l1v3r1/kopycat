package ru.inforion.lab403.kopycat.cores.x86.hardware.processors

import net.sourceforge.argparse4j.inf.ArgumentParser
import ru.inforion.lab403.common.extensions.hex4
import ru.inforion.lab403.common.extensions.hex8
import ru.inforion.lab403.common.extensions.toLong
import ru.inforion.lab403.common.extensions.variable
import ru.inforion.lab403.kopycat.cores.base.GenericSerializer
import ru.inforion.lab403.kopycat.cores.base.abstracts.ACPU
import ru.inforion.lab403.kopycat.cores.base.enums.AccessAction
import ru.inforion.lab403.kopycat.cores.x86.enums.x86GPR
import ru.inforion.lab403.kopycat.cores.x86.hardware.registers.*
import ru.inforion.lab403.kopycat.cores.x86.hardware.systemdc.x86SystemDecoder
import ru.inforion.lab403.kopycat.cores.x86.instructions.AX86Instruction
import ru.inforion.lab403.kopycat.cores.x86.instructions.cpu.control.Nop
import ru.inforion.lab403.kopycat.cores.x86.operands.x86Register.GPRDW.eip
import ru.inforion.lab403.kopycat.cores.x86.operands.x86Register.SSR.cs
import ru.inforion.lab403.kopycat.interfaces.IInteractive
import ru.inforion.lab403.kopycat.modules.cores.x86Core

/**
 * Created by v.davydov on 07.09.16.
 */

class x86CPU(val x86: x86Core, name: String): ACPU<x86CPU, x86Core, AX86Instruction, x86GPR>(x86, name) {

    enum class Mode { R16, R32 }

    var halted = false
    var defaultSize = false
    // Operand and Address size mode! Not real or protected!
    // Real or protected mode defined by cregs.vpe
    val mode: Mode get() = if (cregs.vpe && defaultSize) Mode.R32 else Mode.R16

    override fun serialize(ctxt: GenericSerializer): Map<String, Any> {
        return mapOf(
                "regs" to regs.serialize(ctxt),
                "sregs" to sregs.serialize(ctxt),
                "flags" to flags.serialize(ctxt),
                "dregs" to dregs.serialize(ctxt),
                "cregs" to cregs.serialize(ctxt),
                "pc" to pc.hex8
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(ctxt: GenericSerializer, snapshot: Map<String, Any>) {
        super.deserialize(ctxt, snapshot)
        regs.deserialize(ctxt, snapshot["regs"] as Map<String, String>)
        sregs.deserialize(ctxt, snapshot["sregs"] as Map<String, String>)
        flags.deserialize(ctxt, snapshot["flags"] as Map<String, String>)
        dregs.deserialize(ctxt, snapshot["dregs"] as Map<String, String>)
        cregs.deserialize(ctxt, snapshot["cregs"] as Map<String, String>)
    }

    override fun reg(index: Int): Long = regs[index].value(x86)
    override fun reg(index: Int, value: Long) = regs[index].value(x86, value)
    override fun count() = regs.count()

    /**
     * Stub for debugging purpose of x86CPU core
     * Read - returns physical CPU.PC address
     * Write - setup only offset of virtual address within cs segment
     */
    override var pc: Long
//        get() = x86.mmu.translate(eip.value(x86), cs.reg, 1, AccessAction.LOAD)
        get() = eip.value(x86)
        set(value) {
            eip.value(x86, value)
//            log.warning { "Setting up x86CPU.PC changing only offset within CS segment -> %04X:%08X = %08X"
//                    .format(cs.value(dev), eip.value(dev), pc) }
        }

    val regs = GPRBank(x86)
    val flags = FLBank(x86)
    val sregs = SSBank(x86)
    val dregs = DBGBank(x86)
    val cregs = CTRLBank(x86)

    private val decoder = x86SystemDecoder(x86, this)

    override fun reset() {
        super.reset()
        decoder.reset()
        regs.reset()
        flags.reset()
        sregs.reset()
        insn = Nop.create(x86)
    }

    override fun decode() {
        insn = decoder.decode(regs.eip)
//        log.info { "cs=${sregs.cs.hex4} eip=${regs.eip.hex8} $insn" }
    }

    override fun execute(): Int {
        regs.eip += insn.size
        insn.execute()
        return 1  // TODO: get from insn.execute()
    }

    override fun stringify(): String {
        val where = x86.mmu.translate(eip.value(x86), cs.reg, 1, AccessAction.LOAD)
        return arrayOf(
                "$name PC = %08X".format(where),
                "%s".format(regs.stringify()),
                "%s".format(sregs.stringify())
        ).joinToString("")
    }

    override fun configure(parent: ArgumentParser?, useParent: Boolean): ArgumentParser? =
            super.configure(parent, useParent)?.apply {
                variable<String>("cs", help = "Code segment")
                variable<String>("eip", help = "Code offset")
            }

    override fun process(context: IInteractive.Context): Boolean {
        if (super.process(context))
            return true

        val cs = context.getString("cs").toLong(16)
        val eip = context.getString("eip").toLong(16)

        context.result = when {
            cs % 8 == 0L ->    // if cs in GDT
                "cs = %04X. eip = %08X".format(cs, eip)
            cs % 4 == 0L ->    // if cs in LDT
                "cs = %04X. It is LDT selector. Check, that LDTR is coincident. IP will be modified".format(cs)
            else -> {          // it is not LDT or GDT
                "Error! cs = %04X. It is not GDT or LDT selector. Command will be ignored".format(cs)
            }
        }

        sregs.cs = cs
        regs.eip = eip

        return true
    }
}