package ru.inforion.lab403.kopycat.modules.cortexm0

import ru.inforion.lab403.common.logging.logger
import ru.inforion.lab403.kopycat.cores.base.bit
import ru.inforion.lab403.kopycat.cores.base.common.Module
import ru.inforion.lab403.kopycat.cores.base.common.ModulePorts
import ru.inforion.lab403.kopycat.cores.base.enums.Datatype.DWORD
import ru.inforion.lab403.kopycat.cores.base.extensions.request
import ru.inforion.lab403.kopycat.cores.base.field
import java.util.logging.Level

@Suppress("unused", "PrivatePropertyName")
/**
 * Created by r.valitov on 15.06.17.
 */

class SCB(parent: Module, name: String) : Module(parent, name) {
    companion object {
        private val log = logger(Level.ALL)
    }

    inner class Ports : ModulePorts(this) {
        val mem = Slave("mem", 0x80)
        val irq = Master("irq", NVIC.INTERRUPT_COUNT)
    }

    override val ports = Ports()

    inner class CPUID_TYP : Register(ports.mem, 0, DWORD, "CPUID", 0x410C_C200, writable = false) {
        var Revision by field(3..0)
        var PartNo by field(15..4)
        var Constant by field(19..16)
        var Variant by field(23..20)
        var Implementer by field(31..24)
    }

    inner class ICSR_TYP : Register(ports.mem, 0x4, DWORD, "ICSR") {
        var VECTACTIVE by field(5..0)
        var VECTPENDING by field(17..12)
        var ISRPENDING by bit(22)
        var PENDSTCLR by bit(25)
        var PENDSTSET by bit(26)
        var PENDSVCLR by bit(27)
        var PENDSVSET by bit(28)
        var NMIPENDSET by bit(31)

        override fun write(ea: Long, ss: Int, size: Int, value: Long) {
            super.write(ea, ss, size, value)
            if (NMIPENDSET == 1) ports.irq.request(2)
            if (PENDSVSET == 1) ports.irq.request(14)
            if (PENDSTSET == 1) ports.irq.request(15)
        }
    }

    inner class AIRCR_TYP : Register(ports.mem, 0xC, DWORD, "AIRCR", 0xFA05_0000) {
        var VECTCLRACTIVE by bit(1)
        var SYSRESETREQ by bit(2)
        var ENDIANESS by bit(15)
        var VECTKEY by field(31..16)
    }

    inner class SCR_TYP : Register(ports.mem, 0x10, DWORD, "SCR") {
        var SLEEPONEXIT by bit(1)
        var SLEEPDEEP by bit(2)
        var SEVEONPEND by bit(4)
    }

    inner class CCR_TYP : Register(ports.mem, 0x14, DWORD, "CCR", 0x204) {
        var UNALIGN_TRP by bit(3)
        var STKALIGN by bit(9)
    }

    inner class SHPR2_TYP : Register(ports.mem, 0x1C, DWORD, "SHPR2") {
        var PRI_11 by field(31..24)
    }

    inner class SHPR3_TYP : Register(ports.mem, 0x20, DWORD, "SHPR3") {
        var PRI_14 by field(23..16)
        var PRI_15 by field(31..24)
    }

    private var CPUID = CPUID_TYP() // CPUID base register
    private var ICSR = ICSR_TYP()   // Interrupt control and state register
    private var AIRCR = AIRCR_TYP() // Application interrupt and reset control register
    private var SCR = SCR_TYP()     // System control register
    private var CCR = CCR_TYP()     // Configuration and control register
    private var SHPR2 = SHPR2_TYP() // System handler priority registers
    private var SHPR3 = SHPR3_TYP()
}