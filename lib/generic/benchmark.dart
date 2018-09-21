

DateTime benchmark(final String what, final DateTime t0) {
  final t1 = DateTime.now();
  print("${t1.difference(t0).inMilliseconds}ms: $what");
  return t1;
}
