package ru.asurkis.olymp.software_engineering.task3

import org.springframework.data.repository.CrudRepository

interface PromoRepository : CrudRepository<Promo, Int>

interface PrizeRepository : CrudRepository<Prize, Int> {
    fun findByPromo(promo: Promo): Iterable<Prize>
}

interface ParticipantRepository : CrudRepository<Participant, Int> {
    fun findByPromo(promo: Promo): Iterable<Participant>
}

interface RaffleRepository : CrudRepository<RaffleResult, Int> {
    fun findByPromo(promo: Promo): Iterable<RaffleResult>
    fun deleteByPromo(promo: Promo)
}
