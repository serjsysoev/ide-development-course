package language.structures

class SpaghettiStack<T> {

    private var nodeOnTop: Node<T>? = null

    private class Node<T>(
        val value: T,
        val parent: Node<T>?
    )

    fun push(value: T) {
        nodeOnTop = Node(value, nodeOnTop)
    }

    fun pop() {
        nodeOnTop = nodeOnTop?.parent
    }

    fun top(): T? = nodeOnTop?.value

    fun lookUpInChain(predicate: (T) -> Boolean): T? {
        var curNode = nodeOnTop
        while (curNode != null) {
            if (predicate(curNode.value)) {
                return curNode.value
            }
            curNode = curNode.parent
        }
        return null
    }
}
