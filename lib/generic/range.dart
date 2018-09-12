import 'package:meta/meta.dart';

/// Returns a int range, inclusively beginning [from] and exclusively
/// running to [to]. The step size is determined by [step].
/// If [step] is not `1`, the value `to - 1` is not guaranteed to be reached.
/// No parameter must be `null`.
Iterable<int> range({
  @required final int to,
  final int from = 0,
  final int step = 1,
}) sync* {
  assert(from != null && to != null && step != null, "Nothing must be null");
  assert(to > from, "Range end must be greater than the range start");
  assert(step > 0, "Step must be positive and not null");

  for (var i = from; i < to; i += step) {
    yield i;
  }
}

/// Returns a double range, inclusively beginning [from] and exclusively
/// running to [to]. The step size is determined by [step].
/// If [step] is not `1`, the value `to - 1` is not guaranteed to be reached.
/// No parameter must be `null`.
Iterable<double> rangeDoubles({
  @required final double to,
  final double from = 0.0,
  final double step = 1.0,
}) sync* {
  assert(from != null && to != null && step != null, "Nothing must be null");
  assert(to > from, "Range end must be greater than the range start");
  assert(step > 0.0, "Step must be positive and not null");

  for (var i = from; i < to; i += step) {
    yield i;
  }
}
