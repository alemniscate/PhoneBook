package phonebook

import java.io.File

fun main(args: Array<String>) {
    val phoneFileName = "D:\\Users\\nagai\\downloads\\directory.txt"
//    val phoneFileName = "D:\\Users\\nagai\\downloads\\test.txt"
    val nameFileName = "D:\\Users\\nagai\\downloads\\find.txt"
//    val nameFileName = "D:\\Users\\nagai\\downloads\\findtest.txt"
//    println(phoneFileName)
//    println(nameFileName)
    val phoneFile = File(phoneFileName)
    val nameFile = File(nameFileName)
    var phoneList = phoneFile.readLines()
    val nameList = nameFile.readLines()
//    println(phoneList.size)
//    println(nameList.size)

    println("Start searching (linear search)...")
    var ls = Speed()
    var count = linearSearch(phoneList as MutableList<String>, nameList as MutableList<String>)
    ls.finish()
    println("Found $count / 500 entries. Time taken: ${ls.time()}")

    println()
    println("Start searching (bubble sort + jump search)...")
    val bj = Speed()
    val bs = Speed()
    bs.setTimeOut(ls.getTime() + 10 * 60 * 1000)
    if (!bubbleSort(phoneList as MutableList<String>, bs)) {
        bs.finish()
        ls = Speed()
        phoneList = phoneFile.readLines()
        count = linearSearch(phoneList as MutableList<String>, nameList as MutableList<String>)
        ls.finish()
        bj.finish()
        println("Found $count / 500 entries. Time taken: ${bj.time()}")
        println("Sorting time: ${bs.time()} - STOPPED, moved to linear search")
        println("Searching time: ${ls.time()}")
    } else {
        val js = Speed()
        count = jumpSearch(phoneList as MutableList<String>, nameList as MutableList<String>)
        js.finish()
        bj.finish()
        println("Found $count / 500 entries. Time taken: ${bj.time()}")
        println("Sorting time: ${bs.time()}")
        println("Searching time: ${js.time()}")
        phoneList = phoneFile.readLines()
    }

    println()
    println("Start searching (quick sort + binary search)...")
    val qb = Speed()
    val qs = Speed()
    val sortList = MutableList<String>(phoneList.size) { "" }
    quickSort(phoneList as MutableList<String>, 0, sortList)
    qs.finish()
    val bis = Speed()
    count = binarySearch(sortList as MutableList<String>, nameList as MutableList<String>)
    bis.finish()
    qb.finish()
    println("Found $count / 500 entries. Time taken: ${qb.time()}")
    println("Sorting time: ${qs.time()}")
    println("Searching time: ${bis.time()}")

    println()
    println("Start searching (hash table)...")
    val hh = Speed()
    val hc = Speed()
    val map = mutableMapOf<String, String>()
    createHash(phoneList as MutableList<String>, map)
    hc.finish()
    val hs = Speed()
    count = hashSearch(map, nameList as MutableList<String>)
    hs.finish()
    hh.finish()
    println("Found $count / 500 entries. Time taken: ${hh.time()}")
    println("Creating time: ${hc.time()}")
    println("Searching time: ${hs.time()}")
}

class Speed {
    val startTime = System.currentTimeMillis()
    var interval: Long = 0
    var timeLimit: Long = 0

    fun finish() {
        interval = System.currentTimeMillis() - startTime
    }

    fun time(): String {
        val min = interval / 60000
        val sec = (interval % 60000) / 1000
        val ms = interval % 1000
        return "$min min. $sec sec. $ms ms."
    }

    fun checkTimeout(): Boolean {
        if (System.currentTimeMillis() - startTime > timeLimit) {
            return true
        }
        return false
    }

    fun setTimeOut(t: Long) {
        timeLimit = t
    }

    fun getTime(): Long {
        return interval
    }
}

fun createHash(phoneList: MutableList<String>, map: MutableMap<String, String>) {
    for (line in phoneList) {
        val key = getName(line)
        val value = getNumber(line)
        map[key] = value
    }
}

fun hashSearch(map: MutableMap<String, String>, nameList: MutableList<String>): Int {
    var count = 0
    for (nline in nameList) {
        if (map.containsKey(nline)) {
            count++
//            println(count)
        }
    }
    return count
}

fun quickSort(sourceList: MutableList<String>, index: Int, sortList: MutableList<String>) {
    if (sourceList.isEmpty()) {
        return
    }
    if (sourceList.size == 1) {
        sortList[index] = sourceList[0]
        return
    }
    val pivot = sourceList.last()
    val leftPart = mutableListOf<String>()
    val equalPart = mutableListOf<String>()
    val rightPart = mutableListOf<String>()
    part(sourceList, pivot, leftPart, equalPart, rightPart)
    val pivotIndex = index + leftPart.size
    for (i in equalPart.indices) {
        sortList[pivotIndex + i] = pivot
    }

    quickSort(leftPart, index, sortList)
    quickSort(rightPart, pivotIndex + equalPart.size, sortList)
}

fun part(sourceList: MutableList<String>, pivot: String, leftPart: MutableList<String>, equalPart: MutableList<String>, rightPart: MutableList<String>) {
    val pivotName = getName(pivot)

    for (line in sourceList) {
        val compName = getName(line)
        when {
            compName == pivotName -> equalPart.add(line)
            compName < pivotName -> leftPart.add(line)
            compName > pivotName -> rightPart.add(line)
        }
    }
}

fun binarySearch(phoneList: MutableList<String>, nameList: MutableList<String>): Int {
    var count = 0
    for (nline in nameList) {
        if (binarySearchExec(nline, phoneList, 0, phoneList.lastIndex)) {
            count++
//            println(count)
        }
    }
    return count
}

fun binarySearchExec(nline: String, phoneList: MutableList<String>, left: Int, right: Int): Boolean {
    if (left > right) {
        return false
    }
    val middle = (left + right) / 2
    val name = getName(phoneList[middle])
    return when {
        name == nline -> true
        name > nline -> binarySearchExec(nline, phoneList, left, middle - 1)
        name < nline -> binarySearchExec(nline, phoneList, middle + 1, right)
        else -> false
    }
}

fun bubbleSort(phoneList: MutableList<String>, speed: Speed): Boolean {
    var endindex = phoneList.lastIndex
    while (endindex > 0) {
        if (speed.checkTimeout()) {
            return false
        }
        //     println(endindex)
        for (i in 0..endindex - 1) {
            if (getName(phoneList[i + 1]) < getName(phoneList[i])) {
                val work = phoneList[i]
                phoneList[i] = phoneList[i + 1]
                phoneList[i + 1] = work
            }
        }
        endindex--
    }
    return true
}

fun getName(line: String): String {
    val strs = line.split(" ")
    var compstr = strs[1]
    if (strs.size > 2) {
        compstr += " " + strs[2]
    }
    return compstr
}

fun getNumber(line: String): String {
    val strs = line.split(" ")
    return strs[0]
}

fun jumpSearch(phoneList: MutableList<String>, nameList: MutableList<String>): Int {
    val step = Math.floor(Math.sqrt(phoneList.size.toDouble())).toInt()
    var count = 0
    for (nline in nameList) {
        var i = 0
        while (true) {
            var lowlimit = i - step
            if (lowlimit < 0) lowlimit = 0
            var j = i
            val compstr = getName(phoneList[j])
            if (compstr >= nline) {
                while (j > lowlimit) {
                    val compstr = getName(phoneList[j])
                    if (compstr == nline) {
                        count++
                        //                     println(count)
                    }
                    j--
                }
            }
            if (i == phoneList.lastIndex) break
            i += step
            if (i >= phoneList.size) {
                i = phoneList.lastIndex
            }
        }
    }
    return count
}

fun linearSearch(phoneList: MutableList<String>, nameList: MutableList<String>): Int {
    var count = 0
    for (nline in nameList) {
        for (pline in phoneList) {
            val compstr = getName(pline)
            if (compstr == nline) {
                count++
                //               println(count)
            }
        }
    }
    return count
}