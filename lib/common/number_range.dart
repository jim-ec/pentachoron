/// Remaps [x] from number range defined by [lowerFrom] and [upperFrom]
/// onto the output number range defined by [lowerTo] and [upperTo].
///
/// Lower bounds do not have to be smaller in value than their
/// upper counterparts.
double remap(
  final double x,
  final double lowerFrom,
  final double upperFrom,
  final double lowerTo,
  final double upperTo,
) =>
    (x - lowerFrom) / (upperFrom - lowerFrom) * (upperTo - lowerTo) + lowerTo;
