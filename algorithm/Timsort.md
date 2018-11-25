# TimSort



TimSort是mergeSort的一种改进，引入binarySort进行子数组的排序，实现优化（原来的子数组排序是采用的选择排序），每次进行子数组合并的时候会进行一些特殊的处理来进行对一些特殊情况的优化。 



**Timsort的核心过程**

​       **TimSort 算法为了减少对升序部分的回溯和对降序部分的性能倒退，将输入按其升序和降序特点进行了分区。排序的输入的单位不是一个个单独的数字，而是一个个的块-分区。其中每一个分区叫一个run。针对这些 run 序列，每次拿一个 run 出来按规则进行合并。每次合并会将两个 run合并成一个 run。合并的结果保存到栈中。合并直到消耗掉所有的 run，这时将栈上剩余的 run合并到只剩一个 run 为止。这时这个仅剩的 run 便是排好序的结果。**

**综上述过程，Timsort算法的过程包括**

**（0）如何数组长度小于某个值，直接用二分插入排序算法**

**（1）找到各个run，并入栈**

**（2）按规则合并run**



TimSort算法是一种起源于归并排序和插入排序的混合排序算法，设计初衷是为了在真实世界中的各种数据中可以有较好的性能。该算法最初是由Tim Peters于2002年在Python语言中提出的。 



Timsort是稳定的算法，当待排序的数组中已经有排序好的数，它的时间复杂度会小于n logn。与其他合并排序一样，Timesrot是稳定的排序算法，最坏时间复杂度是O（n log n）。在最坏情况下，Timsort算法需要的临时空间是n/2，在最好情况下，它只需要一个很小的临时存储空间 



在jdk1.7之后，Arrays类中的sort方法有一个分支判断，当LegacyMergeSort.userRequested为true的情况下，采用legacyMergeSort，否则采用ComparableTimSort。并且在legacyMergeSort的注释上标明了该方法会在以后的jdk版本中废弃，因此以后Arrays类中的sort方法将采用ComparableTimSort类中的sort方法。 



























相关文章

[Timsort事件告诉我们](https://blog.csdn.net/qzy/article/details/78319303)

