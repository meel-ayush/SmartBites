package com.smartbite.app.domain

import com.smartbite.app.data.model.PlatformPrice
import kotlin.math.roundToInt

object PriceCalculator {

    private fun round2(value: Double) = (value * 100.0).roundToInt() / 100.0

    fun getAllPrices(basePrice: Double): List<PlatformPrice> {
        val grab = buildGrab(basePrice)
        val panda = buildPanda(basePrice)
        val shopee = buildShopee(basePrice)
        val cheapest = minOf(grab.grandTotal, panda.grandTotal, shopee.grandTotal)
        return listOf(
            grab.copy(isBestDeal = grab.grandTotal == cheapest),
            panda.copy(isBestDeal = panda.grandTotal == cheapest),
            shopee.copy(isBestDeal = shopee.grandTotal == cheapest)
        )
    }

    private fun buildGrab(base: Double): PlatformPrice {
        val food = round2(base * Constants.GRAB_MARKUP)
        val del = Constants.GRAB_DELIVERY
        val tax = round2((food + del) * Constants.TAX_RATE)
        return PlatformPrice("GrabFood", food, del, tax, round2(food + del + tax),
            "18–28 min", "GrabUnlimited: Free delivery + 20% off for subscribers",
            false, Constants.GRAB_URL, "FASTEST 🚀")
    }

    private fun buildPanda(base: Double): PlatformPrice {
        val food = round2(base * Constants.FOODPANDA_MARKUP)
        val del = Constants.FOODPANDA_DELIVERY
        val tax = round2((food + del) * Constants.TAX_RATE)
        return PlatformPrice("Foodpanda", food, del, tax, round2(food + del + tax),
            "25–38 min", "Panda Pro: Free delivery on orders above RM 15",
            false, Constants.FOODPANDA_URL, "POPULAR 👍")
    }

    private fun buildShopee(base: Double): PlatformPrice {
        val food = round2(base * Constants.SHOPEE_MARKUP)
        val del = Constants.SHOPEE_DELIVERY
        val tax = round2((food + del) * Constants.TAX_RATE)
        return PlatformPrice("ShopeeFood", food, del, tax, round2(food + del + tax),
            "32–48 min", "ShopeePay: Earn 8% cashback on this order",
            false, Constants.SHOPEE_URL, "BEST DEAL ⭐")
    }
}
