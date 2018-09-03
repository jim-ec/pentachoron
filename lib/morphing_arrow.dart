import 'package:flutter/material.dart';

class MorphingArrow extends StatelessWidget {
  final double advance;
  final Color color;

  const MorphingArrow({
    Key key,
    @required this.advance,
    @required this.color,
  }) : super(key: key);

  @override
  Widget build(final BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: _MorphingArrowPainter(advance, color),
        ),
      );
}

class _MorphingArrowPainter extends CustomPainter {
  final double advance;
  final Paint arrowPaint;

  /// Three points defining the arrow in a unit space.
  static const points = <Offset>[
    Offset(0.3, 0.4),
    Offset(0.5, 0.6),
    Offset(0.7, 0.4),
  ];

  _MorphingArrowPainter(this.advance, final Color color)
      : arrowPaint = Paint()
          ..color = color
          ..strokeWidth = 2.0
          ..strokeCap = StrokeCap.round
          ..strokeJoin = StrokeJoin.round;

  /// Linearly interpolation between the actual arrow shape as given by
  /// [points] and its vertically mirrored version.
  /// This effectively morphs between the default down arrow and the up
  /// arrow.
  Offset lerpPoint(final Offset point) =>
      Offset.lerp(point, Offset(point.dx, 1.0 - point.dy), advance);

  @override
  bool shouldRepaint(final _MorphingArrowPainter oldDelegate) =>
      oldDelegate.advance != advance;

  @override
  void paint(final Canvas canvas, final Size size) => points
          .map(lerpPoint)
          .map((point) => point.scale(size.width, size.height))
          .reduce((from, to) {
        canvas.drawLine(from, to, arrowPaint);
        return to;
      });
}
