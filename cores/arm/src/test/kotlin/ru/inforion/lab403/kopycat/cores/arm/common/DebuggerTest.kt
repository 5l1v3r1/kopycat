package ru.inforion.lab403.kopycat.cores.arm.common

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.inforion.lab403.common.extensions.*
import ru.inforion.lab403.kopycat.cores.base.common.Module
import ru.inforion.lab403.kopycat.cores.base.common.ModuleBuses
import ru.inforion.lab403.kopycat.cores.base.enums.Datatype
import ru.inforion.lab403.kopycat.gdbstub.GDB_BPT
import ru.inforion.lab403.kopycat.gdbstub.GDB_BPT.*
import ru.inforion.lab403.kopycat.library.types.Resource
import ru.inforion.lab403.kopycat.loader.KopycatHelper
import ru.inforion.lab403.kopycat.modules.cores.ARMv7Core
import ru.inforion.lab403.kopycat.modules.debuggers.ARMDebugger
import ru.inforion.lab403.kopycat.modules.memory.RAM

class DebuggerTest: Module(null, "ARM Debugger Test") {
    inner class Buses: ModuleBuses(this) { val mem = Bus("mem") }
    override val buses = Buses()
    private val arm = ARMv7Core(this, "arm", 48.MHz, 1.0)
    private val ram1 = RAM(this, "ram1", 0x1000, Resource("binaries/strcpy.bin"))
    private val dbg = ARMDebugger(this, "dbg")

    init {
        arm.ports.mem.connect(buses.mem)
        ram1.ports.mem.connect(buses.mem)
        arm.cpu.pc = 0x0
        arm.cpu.status.ISETSTATE = 1
        dbg.ports.breakpoint.connect(buses.mem)
        KopycatHelper.initializeToken(System.getenv("KC_LICENCE"))
        initializeAndResetAsTopInstance()
    }

    private fun prepareStrings() {
        val str = "Hello World"
        val address1 = 0x100L
        val address2 = 0x200L
        store(address1, str)
        regs(r0 = address1, r1 = address2)
    }

    private fun regs(r0: Long = 0, r1: Long = 0, r2: Long = 0, r3: Long = 0, r4: Long = 0,
                     r5: Long = 0, r6: Long = 0, r7: Long = 0, r8: Long = 0, r9: Long = 0,
                     r10: Long = 0, r11: Long = 0, r12: Long = 0, r13: Long = 0, r14: Long = 0) {
        arm.cpu.regs.r0 = r0
        arm.cpu.regs.r1 = r1
        arm.cpu.regs.r2 = r2
        arm.cpu.regs.r3 = r3
        arm.cpu.regs.r4 = r4
        arm.cpu.regs.r5 = r5
        arm.cpu.regs.r6 = r6
        arm.cpu.regs.r7 = r7
        arm.cpu.regs.r8 = r8
        arm.cpu.regs.r9 = r9
        arm.cpu.regs.r10 = r10
        arm.cpu.regs.r11 = r11
        arm.cpu.regs.r12 = r12
        arm.cpu.regs.spMain = r13
        arm.cpu.regs.lr = r14
    }

    private fun flags(n: Long = 0, z: Long = 0, c: Long = 0, v: Long = 0) {
        arm.cpu.flags.n = n.toBool()
        arm.cpu.flags.z = z.toBool()
        arm.cpu.flags.c = c.toBool()
        arm.cpu.flags.v = v.toBool()
    }

    private fun status(q: Long = 0, ge: Long = 0) {
        arm.cpu.status.q = q.toBool()
        arm.cpu.status.ge = ge.asLong
    }

    private fun load(address: Long, size: Int): String = arm.load(address, size).hexlify()
    private fun load(address: Long, dtyp: Datatype): Long = arm.read(dtyp, address, 0)
    private fun store(address: Long, data: String) = arm.store(address, data.unhexlify())
    private fun store(address: Long, data: Long, dtyp: Datatype) = arm.write(dtyp, address, data, 0)

    private fun assertPC(expected: Long, actual: Long, type: String = "PC") =
            Assert.assertEquals("$type error: 0x${expected.hex8} != 0x${actual.hex8}", expected, actual)

    @Before fun resetTest() {
        arm.reset()
        dbg.reset()
        prepareStrings()
    }

    private fun setBreakpoint(address: Long, btyp: GDB_BPT = SOFTWARE) = dbg.bptSet(btyp, address)
    private fun deleteBreakpoint(address: Long) = dbg.bptClr(address)

    @Test fun bptExecTest() {
        val expected = 0x6L
        setBreakpoint(expected)
        val def = async { dbg.cont() }
        runBlocking { def.await() }
        assertPC(expected, dbg.cpu.pc)
        dbg.step()
        assertPC(expected + 2, arm.pc)
    }

    @Test fun bptWriteTest() {
        val expected = 0x8L
        setBreakpoint(0x100, WRITE)
        val def = async { dbg.cont() }
        runBlocking { def.await() }
        assertPC(expected, dbg.cpu.pc)
    }

    @Test fun bptReadTest() {
        val expected = 0x4L
        setBreakpoint(0x200, READ)
        val def = async { dbg.cont() }
        runBlocking { def.await() }
        assertPC(expected, dbg.cpu.pc)
    }

    @Test fun bptAddDelete() {
        val expected = 0x8L
        setBreakpoint(0x100, WRITE)
        setBreakpoint(0x200, READ)
        setBreakpoint(0x200, READ)
        setBreakpoint(0x200, READ)
        deleteBreakpoint(0x200)
        deleteBreakpoint(0x200)
        deleteBreakpoint(0x200)
        val def = async { dbg.cont() }
        runBlocking { def.await() }
        assertPC(expected, dbg.cpu.pc)
    }

}