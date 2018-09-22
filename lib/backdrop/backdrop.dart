import 'package:flutter/material.dart';
import 'package:tesserapp/backdrop/morphing_arrow.dart';
import 'package:tesserapp/button.dart';
import 'package:tesserapp/generic/number_range.dart';

class Backdrop extends StatefulWidget {
  final Widget backLayer;
  final Widget frontLayer;

  const Backdrop({
    Key key,
    @required this.backLayer,
    @required this.frontLayer,
  }) : super(key: key);

  @override
  _BackdropState createState() => _BackdropState(backLayer, frontLayer);
}

class _BackdropState extends State<Backdrop>
    with SingleTickerProviderStateMixin {
  /// The height of the back bar.
  /// https://material.io/design/components/backdrop.html#specs
  static const backBarHeight = 56.0;

  /// The remaining visible height of the front layer, when completely hidden.
  ///
  /// https://material.io/design/components/backdrop.html#specs
  static const hiddenFrontLayerHeight = 48.0;

  /// The border radius of the front layer.
  ///
  /// A bit larger than the specs define:
  /// https://material.io/design/components/backdrop.html#specs
  static const frontLayerBorderRadius =
      BorderRadius.vertical(top: Radius.circular(24.0));

  final Widget backLayer;
  final Widget frontLayer;

  /// The controller used to animate the front layer.
  /// 0.0 means that the front layer is completely opened,
  /// whereas 1.0 indicates a completely closed front layer.
  AnimationController controller;

  @override
  void initState() {
    super.initState();

    controller = AnimationController(vsync: this)
      ..addListener(() {
        setState(() {});
      });
  }

  _BackdropState(this.backLayer, this.frontLayer);

  @override
  Widget build(BuildContext context) => LayoutBuilder(
        builder: (context, constraints) => Stack(
              fit: StackFit.expand,
              children: <Widget>[
                Material(
                  color: Theme.of(context).primaryColor,
                ),
                Positioned(
                  top: 0.0,
                  height: backBarHeight,
                  left: 0.0,
                  right: 0.0,
                  child: buildBackBar(),
                ),
                // Back layer:
                Positioned(
                  top: backBarHeight,
                  bottom: hiddenFrontLayerHeight,
                  left: 0.0,
                  right: 0.0,
                  child: backLayer,
                ),
                Positioned(
                  top: backBarHeight +
                      controller.value *
                          (constraints.biggest.height -
                              hiddenFrontLayerHeight -
                              backBarHeight),
                  height: constraints.biggest.height - backBarHeight,
                  left: 0.0,
                  right: 0.0,
                  child: buildFrontLayer(constraints),
                ),
              ],
            ),
      );

  Widget buildBackBar() => Material(
        color: Theme.of(context).primaryColor,
        elevation: remap(controller.value, 0.0, 1.0, -2.0, 2.0)
            .clamp(0.0, double.infinity),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          child: Stack(
            children: <Widget>[
              Positioned.fill(
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Center(
                    child: Text(
                      "PENTACHORON",
                      style: Theme.of(context).primaryTextTheme.title,
                    ),
                  ),
                ),
              ),
              Positioned.fill(
                left: null,
                top: 16.0,
                bottom: 16.0,
                right: 16.0,
                child: Container(
                  width: 56.0,
                  child: Button(
                    onPressed: () {
                      switch (controller.status) {
                        case AnimationStatus.forward:
                        case AnimationStatus.completed:
                          controller.fling(velocity: -0.01);
                          break;
                        case AnimationStatus.reverse:
                        case AnimationStatus.dismissed:
                          controller.fling(velocity: 0.01);
                          break;
                      }
                    },
                    child: MorphingArrow(
                      color: Theme.of(context).primaryTextTheme.title.color,
                      advance: controller.value,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      );

  Widget buildFrontLayer(final BoxConstraints constraints) {
    final List<Widget> stackedWidget = [
      Material(
        borderRadius: frontLayerBorderRadius,
        elevation: 24.0,
        color: Theme.of(context).scaffoldBackgroundColor,
        child: frontLayer,
      )
    ];

    // Front layer drag listener is only attached if the front layer
    // is currently *not* fully opened.
    if (controller.status != AnimationStatus.dismissed) {
      stackedWidget.add(GestureDetector(
        onVerticalDragUpdate: (final dragDetails) {
          setState(() {
            controller.value +=
                dragDetails.primaryDelta / (constraints.biggest.height - 100);
          });
        },
        onVerticalDragEnd: (final dragDetails) {
          final flingVelocity = dragDetails.velocity.pixelsPerSecond.dy /
              constraints.biggest.height;
          if (flingVelocity < 0.2 &&
              flingVelocity >= 0.0 &&
              controller.value < 0.8) {
            controller.fling(velocity: -1.0);
          } else {
            controller.fling(velocity: flingVelocity);
          }
        },
        onTap: () {
          switch (controller.status) {
            case AnimationStatus.forward:
            case AnimationStatus.completed:
              controller.fling(velocity: -.01);
              break;
            default:
              break;
          }
        },
        onHorizontalDragStart: (final details) {},
        onDoubleTap: () {},
        onLongPress: () {},
      ));
    }

    return Stack(
      children: stackedWidget,
    );
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}
