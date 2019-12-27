package ru.inforion.lab403.kopycat.cores.x86.hardware.systemdc.decoders

import ru.inforion.lab403.kopycat.cores.x86.hardware.systemdc.Prefixes
import ru.inforion.lab403.kopycat.cores.x86.hardware.x86OperandStream
import ru.inforion.lab403.kopycat.cores.x86.instructions.AX86Instruction
import ru.inforion.lab403.kopycat.modules.cores.x86Core

/**
 * Created by a.gladkikh on 25.10.16.
 */

class SimpleDC(core: x86Core, val construct: (x86Core, ByteArray, Prefixes) -> AX86Instruction, val size: Int = 1) :
        ADecoder<AX86Instruction>(core) {
    override fun decode(s: x86OperandStream, prefs: Prefixes): AX86Instruction {
        repeat(size - 1) { s.readByte() }
        return construct(core, s.data, prefs)
    }
}