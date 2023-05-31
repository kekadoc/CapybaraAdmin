package kotlinx.coroutines.flow

fun <T> flowOf(block: suspend () -> T): Flow<T> = flow { emit(block()) }

fun <T, R> Flow<T>.extract(block: T.() -> R): Flow<R> = map { block.invoke(it) }

fun <T, R> Flow<T>.swap(block: () -> R): Flow<R> = map { block.invoke() }

fun <T, R> Flow<Collection<T>>.mapElements(
    transform: (T) -> R,
): Flow<List<R>> = map { collection -> collection.map(transform) }

inline fun <reified T> Flow<List<Flow<T>>>.combineLatest(): Flow<List<T>> =
    flatMapLatest { combine(it) { it.toList() } }