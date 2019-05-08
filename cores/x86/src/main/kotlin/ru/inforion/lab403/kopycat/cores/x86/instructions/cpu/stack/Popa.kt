package ru.inforion.lab403.kopycat.cores.x86.instructions.cpu.stack

import ru.inforion.lab403.kopycat.cores.x86.enums.x86GPR
import ru.inforion.lab403.kopycat.cores.x86.hardware.systemdc.Prefixes
import ru.inforion.lab403.kopycat.cores.x86.instructions.AX86Instruction
import ru.inforion.lab403.kopycat.cores.x86.operands.x86Register
import ru.inforion.lab403.kopycat.cores.x86.x86utils
import ru.inforion.lab403.kopycat.modules.cores.x86Core

/**
 * Created by davydov_vn on 22.09.16.
 */
class Popa(core: x86Core, opcode: ByteArray, prefs: Prefixes):
        AX86Instruction(core, Type.VOID, opcode, prefs) {
    override val mnem = "popa"

    override fun execute() {
        val edi = x86utils.pop(core, prefs.opsize, prefs)
        val esi = x86utils.pop(core, prefs.opsize, prefs)
        val ebp = x86utils.pop(core, prefs.opsize, prefs)
        x86Register.gpr(prefs.opsize, x86GPR.ESP).plus(core, prefs.opsize.bytes)
        val ebx = x86utils.pop(core, prefs.opsize, prefs)
        val edx = x86utils.pop(core, prefs.opsize, prefs)
        val ecx = x86utils.pop(core, prefs.opsize, prefs)
        val eax = x86utils.pop(core, prefs.opsize, prefs)
        x86Register.gpr(prefs.opsize, x86GPR.EDI).value(core, edi)
        x86Register.gpr(prefs.opsize, x86GPR.ESI).value(core, esi)
        x86Register.gpr(prefs.opsize, x86GPR.EBP).value(core, ebp)
        x86Register.gpr(prefs.opsize, x86GPR.EBX).value(core, ebx)
        x86Register.gpr(prefs.opsize, x86GPR.EDX).value(core, edx)
        x86Register.gpr(prefs.opsize, x86GPR.ECX).value(core, ecx)
        x86Register.gpr(prefs.opsize, x86GPR.EAX).value(core, eax)
    }
}