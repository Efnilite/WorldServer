package dev.efnilite.ws.group

/**
 * Represents a group of worlds which share some values.
 */
data class Shared(val shareType: ShareType, val worlds: Set<World>)