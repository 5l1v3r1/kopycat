package ru.inforion.lab403.kopycat.cores.v850es.enums

/**
 * Created by r.valitov on 07.07.17.
 */
enum class ExcCode(val code: Int) {
    IncorrectSegment(0x04),
    DivisionByZero(0x05),
    Overflow(0x06)
}