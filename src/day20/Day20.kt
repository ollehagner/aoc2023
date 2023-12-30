package day20

import common.println
import common.readInput
import day20.FlipFlopModule.State.OFF
import day20.Pulse.HIGH
import day20.Pulse.LOW
import java.util.LinkedList

fun main() {
    val router = Router()
    val testinput = readInput("day20/testinput")
    val input = readInput("day20/input")
    part1(input, router).println()
}

fun part1(input: List<String>, router: Router): Int {
    val modules = input.map { Module.parse(it, router) }
    modules.forEach { module ->
        modules
            .filter { it.destinations().contains(module.id()) }
            .map { it.id() }
            .forEach { module.addSource(it) }

    }

    repeat(1000) {
        var sequenceNumber = 0L
        router.sendTo("broadcaster", Message("button", LOW, sequenceNumber))
        while(modules.any { it.hasMessages() }) {
            modules.forEach { it.process(sequenceNumber) }
            sequenceNumber++
        }
    }
    return router.lowPulses * router.highPulses
}

class Router {
    var destinations = mutableMapOf<String, Module>()
    var lowPulses = 0
    var highPulses = 0

    fun add(module: Module) {
        destinations[module.id()] = module
    }

    fun sendTo(moduleId: String, message: Message) {
        if(message.pulse == LOW) lowPulses++ else highPulses++
        if(destinations.contains(moduleId)) {
            destinations[moduleId]!!.receive(message)
        }
    }
}

interface Module {

    fun hasMessages(): Boolean

    fun id(): String

    fun receive(message: Message)

    fun process(sequenceNumber: Long)

    fun addSource(module: String)

    fun destinations(): List<String>

    companion object {
        fun parse(input: String, router: Router): Module {
            val name = input.substringBefore("->").trim()
            return when {
                name.startsWith("%") -> FlipFlopModule(name.drop(1), input.substringAfter("->").split(",").map { it.trim() }.toList(), router)
                name.startsWith("&") -> ConjunctionModule(name.drop(1), input.substringAfter("->").split(",").map { it.trim() }.toList(), router)
                else -> BroadcastModule(name, input.substringAfter("->").split(",").map { it.trim() }.toList(), router)
            }
        }
    }
}

class FlipFlopModule(val id: String, private val destinations: List<String>, private val router: Router): Module {

    init {
        router.add(this)
    }

    private val queue = LinkedList<Message>()
    private var state = OFF

    override fun hasMessages(): Boolean {
        return queue.isNotEmpty()
    }

    override fun id(): String {
        return id
    }

    override fun receive(message: Message) {
        queue.addLast(message)
    }

    override fun process(sequenceNumber: Long) {
        while(queue.peek()?.sequenceNumber == sequenceNumber) {
            val message = queue.pop()
            val pulse = message.pulse
            if(pulse == LOW) {
                val pulseToSend = if(state == OFF) HIGH else LOW
                val messageToSend = Message(id, pulseToSend, sequenceNumber + 1)
                destinations.forEach { destination ->
                    router.sendTo(destination, messageToSend)
                }
                state = state.flip()
            }
        }
    }

    override fun addSource(module: String) {
        //no-op
    }

    override fun destinations(): List<String> {
        return destinations
    }

    enum class State {
        ON, OFF;
        fun flip(): State {
            return if(this == ON) OFF else ON
        }
    }
}

class BroadcastModule(val id: String, val destinations: List<String>, val router: Router): Module {

    init {
        router.add(this)
    }

    private val queue = LinkedList<Message>()

    override fun hasMessages(): Boolean {
        return queue.isNotEmpty()
    }

    override fun id(): String {
        return id
    }

    override fun receive(message: Message) {
        queue.addLast(message)
    }

    override fun process(sequenceNumber: Long) {
        while(queue.peek()?.sequenceNumber == sequenceNumber) {
            val pulseToSend = queue.pop().pulse
            val message = Message(id, pulseToSend, sequenceNumber + 1)
            destinations.forEach { router.sendTo(it, message) }
        }
    }

    override fun addSource(module: String) {
        //no-op
    }

    override fun destinations(): List<String> {
        return destinations
    }
}

class ConjunctionModule(val id: String, val destinations: List<String>, val router: Router): Module {

    init {
        router.add(this)
    }

    override fun hasMessages(): Boolean {
        return queue.isNotEmpty()
    }

    private val received = mutableMapOf<String, Pulse>()
    private val queue = LinkedList<Message>()

    override fun id(): String {
        return id
    }

    override fun receive(message: Message) {
        received[message.sender] = message.pulse
        if(received.values.all { it == HIGH }) queue.addLast(message.copy(pulse = LOW)) else queue.addLast(message.copy(pulse = HIGH))
    }

    override fun process(sequenceNumber: Long) {
        while(queue.peek()?.sequenceNumber == sequenceNumber) {
            val pulseToSend = queue.pop().pulse
            val messageToSend = Message(id, pulseToSend, sequenceNumber + 1)
            destinations.forEach {destination ->
                router.sendTo(destination, messageToSend)
            }
        }
    }

    override fun addSource(module: String) {
        received[module] = LOW
    }

    override fun destinations(): List<String> {
        return destinations
    }

}

enum class Pulse {
    HIGH, LOW
}

data class Message(val sender: String, val pulse: Pulse, val sequenceNumber: Long)

