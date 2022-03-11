package ru.asurkis.olymp.software_engineering.task3

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@ResponseStatus(HttpStatus.NOT_FOUND)
class ResourceNotFoundException : RuntimeException()

@RestController
class Controllers(
    val promoRepository: PromoRepository,
    val prizeRepository: PrizeRepository,
    val participantRepository: ParticipantRepository,
) {
    @PostMapping("/promo")
    fun postPromo(@RequestBody promo: Promo) = promoRepository.save(promo).id

    @GetMapping("/promo")
    fun promoAll() = promoRepository.findAll()

    @GetMapping("/promo/{id}")
    fun promoId(@PathVariable id: Int): PromoDescription {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        val participants = participantRepository.findByPromo(promo).map {
            ParticipantDescription(
                id = it.id,
                name = it.name
            )
        }
        val prizes = prizeRepository.findByPromo(promo).map {
            PrizeDescription(
                id = it.id,
                description = it.description
            )
        }
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

    @PostMapping("/promo/{id}/participant")
    fun promoPostParticipant(@PathVariable id: Int, @RequestBody participant: Participant): Int {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        participant.promo = promo
        return participantRepository.save(participant).id
    }

    @DeleteMapping("/promo/{promoId}/participant/{participantId}")
    fun promoDeleteParticipant(@PathVariable promoId: Int, @PathVariable participantId: Int) {
        promoRepository.findById(promoId).orElseThrow { ResourceNotFoundException() }
        participantRepository.deleteById(participantId)
    }

    @PostMapping("/promo/{id}/prize")
    fun promoPostPrize(@PathVariable id: Int, @RequestBody prize: Prize): Int {
        val promo = promoRepository.findById(id).orElseThrow { ResourceNotFoundException() }
        prize.promo = promo
        return prizeRepository.save(prize).id
    }

    @DeleteMapping("/promo/{promoId}/prize/{prizeId}")
    fun promoDeletePrize(@PathVariable promoId: Int, @PathVariable prizeId: Int) {
        promoRepository.findById(promoId).orElseThrow { ResourceNotFoundException() }
        prizeRepository.deleteById(prizeId)
    }

    @PostMapping("/promo/{id}/raffle")
    fun raffle(@PathVariable id: Int) {

    }
}
