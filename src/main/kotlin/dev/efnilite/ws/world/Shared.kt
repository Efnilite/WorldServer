package dev.efnilite.ws.world

/**
 * Represents a group of worlds which share some values.
 */
data class Shared(val name: String, val shareType: ShareType, val worlds: Set<World>)