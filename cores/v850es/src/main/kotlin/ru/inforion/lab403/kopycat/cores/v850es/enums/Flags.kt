package ru.inforion.lab403.kopycat.cores.v850es.enums

/**
 * Created by r.valitov on 01.06.17.
 */

enum class Flags (val bit: Int) {
    Z(0),
    S(1),
    OV(2),
    CY(3),
    SAT(4),
    ID(5),
    EP(6),
    NP(7);
}