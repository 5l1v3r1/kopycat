package ru.inforion.lab403.kopycat.modules.cortexm0

import ru.inforion.lab403.common.logging.logger
import ru.inforion.lab403.kopycat.cores.base.bit
import ru.inforion.lab403.kopycat.cores.base.common.Module
import ru.inforion.lab403.kopycat.cores.base.common.ModulePorts
import ru.inforion.lab403.kopycat.cores.base.common.SystemClock
import ru.inforion.lab403.kopycat.cores.base.enums.Datatype.DWORD
import ru.inforion.lab403.kopycat.cores.base.extensions.request
import ru.inforion.lab403.kopycat.cores.base.field
import ru.inforion.lab403.kopycat.modules.PIN
import java.util.logging.Level

@Suppress("unused", "PrivatePropertyName")
/**
 * Created by r.valitov on 15.06.17.
 */

class STK(parent: Module, name: String) : Module(parent, name) {
    companion object {
        val log = logger(Level.FINE)
    }

    inner class Ports : ModulePorts(this) {
        val mem = Slave("mem", 0x20)
        val irq = Master("irq", PIN)
    }

    override val ports = Ports()

    private val counter = object : SystemClock.PeriodicalTimer("STK") {
        override fun trigger() {
            super.trigger()
            STK_CVR.CURRENT -= 1
            if (STK_CVR.CURRENT == 0) {
                STK_CVR.CURRENT = STK_RVR.RELOAD
                STK_CSR.COUNTFLAG = 1
                if (STK_CSR.TICKINT == 1) {
                    ports.irq.request(0)
                    log.finer { "$name -> latch value=${STK_RVR.data} reached -> interrupt request at %,d us".format(core.clock.time()) }
                }
            }
        }
    }

    private inner class STK_CSR_TYP : Register(ports.mem, 0x0, DWORD, "STK_CSR", 0x0000_0004) {
        var ENABLE by bit(0)
        var TICKINT by bit(1)
        var CLKSOURCE by bit(2)
        var COUNTFLAG by bit(16)
        override fun write(ea: Long, ss: Int, size: Int, value: Long) {
            super.write(ea, ss, size, value)
            if (ENABLE == 1) {
                STK_CVR.CURRENT = STK_RVR.RELOAD
                counter.enabled = true
            }
        }
    }

    private inner class STK_RVR_TYP : Register(ports.mem, 0x4, DWORD, "STK_RVR") {
        var RELOAD by field(23..0)
    }

    private inner class STK_CVR_TYP : Register(ports.mem, 0x8, DWORD, "STK_CVR") {
        var CURRENT by field(23..0)
        override fun write(ea: Long, ss: Int, size: Int, value: Long) {
            super.write(ea, ss, size, value)
            STK_CSR.COUNTFLAG = 0
            CURRENT = 0
        }
    }

    private inner class STK_CALIB_TYP : Register(ports.mem, 0xC, DWORD, "STK_CALIB", writable = false) {
        var TENMS by field(23..0)
        var SKEW by bit(30)
        var NOREF by bit(31)
    }

    private var STK_CSR = STK_CSR_TYP()     // SysTick control and status register
    private var STK_RVR = STK_RVR_TYP()     // SysTick reload value register
    private var STK_CVR = STK_CVR_TYP()     // SysTick current value register
    private var STK_CALIB = STK_CALIB_TYP() // SysTick calibration value register

    override fun initialize(): Boolean {
        if (!super.initialize()) return false
        core.clock.connect(counter)
        return true
    }
}