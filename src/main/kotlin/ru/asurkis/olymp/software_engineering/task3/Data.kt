package ru.asurkis.olymp.software_engineering.task3

import javax.persistence.*

data class PromoDescription(
    val id: Int,
    val name: String,
    val description: String,
    val prizes: Iterable<PrizeDescription>,
    val participants: Iterable<ParticipantDescription>
)

data class ParticipantDescription(
    val id: Int,
    val name: String
)

data class PrizeDescription(
    val id: Int,
    val description: String
)

data class RaffleResultDescription(
    val participant: ParticipantDescription,
    val prize: PrizeDescription
)

@Entity
data class Promo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    var name: String,
    var description: String = "",
)

@Entity
data class Prize(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    var description: String,

    @ManyToOne
    var promo: Promo
)

@Entity
data class Participant(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    var name: String,

    @ManyToOne
    var promo: Promo
)

@Entity
data class RaffleResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int,
    @ManyToOne
    var winner: Participant,
    @ManyToOne
    var prize: Prize
)
