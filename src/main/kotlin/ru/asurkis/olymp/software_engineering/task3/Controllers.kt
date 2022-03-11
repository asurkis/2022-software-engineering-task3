package ru.asurkis.olymp.software_engineering.task3

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException : RuntimeException()

@ResponseStatus(HttpStatus.CONFLICT)
class ConflictException : RuntimeException()

@RestController
class Controllers(
    val promoRepository: PromoRepository,
    val prizeRepository: PrizeRepository,
    val participantRepository: ParticipantRepository,
    val raffleRepository: RaffleRepository
) {
    @PostMapping("/promo")
    fun postPromo(@RequestBody promo: Promo) = promoRepository.save(promo).id

    @GetMapping("/promo")
    fun promoAll() = promoRepository.findAll()

    @GetMapping("/promo/{id}")
    fun promoId(@PathVariable id: Int): PromoDescription {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        val participants = participantRepository.findByPromo(promo).map { describeParticipant(it) }
        val prizes = prizeRepository.findByPromo(promo).map { describePrize(it) }
        return PromoDescription(
            id = promo.id,
            name = promo.name,
            description = promo.description,
            prizes = prizes,
            participants = participants
        )
    }

    @PutMapping("/promo/{id}")
    fun promoPut(@PathVariable id: Int, @RequestBody param: Promo) {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        promo.name = param.name
        promo.description = param.description
        promoRepository.save(promo)
    }

    @DeleteMapping("/promo/{id}")
    fun promoDelete(@PathVariable id: Int) = promoRepository.deleteById(id)

    @PostMapping("/promo/{id}/prize")
    fun promoPostPrize(@PathVariable id: Int, @RequestBody body: PrizeForm): Int {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        val prize = Prize(id = 0, description = body.description, promo = promo)
        return prizeRepository.save(prize).id
    }

    @DeleteMapping("/promo/{promoId}/prize/{prizeId}")
    fun promoDeletePrize(@PathVariable promoId: Int, @PathVariable prizeId: Int) {
        promoRepository.findById(promoId).orElseThrow { ResourceNotFoundException() }
        prizeRepository.deleteById(prizeId)
    }

    @PostMapping("/promo/{id}/participant")
    fun promoPostParticipant(@PathVariable id: Int, @RequestBody body: ParticipantForm): Int {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        val participant = Participant(id = 0, name = body.name, promo = promo)
        return participantRepository.save(participant).id
    }

    @DeleteMapping("/promo/{promoId}/participant/{participantId}")
    fun promoDeleteParticipant(@PathVariable promoId: Int, @PathVariable participantId: Int) {
        promoRepository.findById(promoId).orElseThrow { ResourceNotFoundException() }
        participantRepository.deleteById(participantId)
    }

    @PostMapping("/promo/{id}/raffle")
    fun raffle(@PathVariable id: Int): List<RaffleResultDescription> {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        val participants = participantRepository.findByPromo(promo).toList()
        val prizes = prizeRepository.findByPromo(promo).toList()
        var list = raffleRepository.findByPromo(promo).toList()
        if (participants.size != prizes.size) {
            throw ConflictException()
        }
        if (list.size != prizes.size) {
            raffleRepository.deleteByPromo(promo)
            list = participants.zip(prizes.shuffled()).map {
                RaffleResult(
                    id = 0,
                    winner = it.first,
                    prize = it.second,
                    promo = promo
                )
            }
            list = raffleRepository.saveAll(list).toList()
        }
        return list.map { describeRaffleResult(it) }
    }
}
