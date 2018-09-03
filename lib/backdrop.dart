import 'package:flutter/material.dart';

class Backdrop extends StatefulWidget {
  final Widget backBar;
  final Widget backLayer;
  final Widget frontLayer;

  const Backdrop({
    Key key,
    @required this.backBar,
    @required this.backLayer,
    @required this.frontLayer,
  }) : super(key: key);

  @override
  _BackdropState createState() =>
      _BackdropState(backBar, backLayer, frontLayer);
}

class _BackdropState extends State<Backdrop>
    with SingleTickerProviderStateMixin {
  /// The height of the back bar, matching the height of the app bar
  /// as per Material design specifications.
  ///
  /// https://material.io/design/components/app-bars-top.html#specs
  static const backBarHeight = 56.0;

  /// The remaining visible height of the front layer, when completely hidden.
  static const hiddenFrontLayerHeight = 64.0;

  final Widget backBar;
  final Widget backLayer;
  final Widget frontLayer;
  AnimationController controller;

  @override
  void initState() {
    super.initState();

    controller = AnimationController(vsync: this)
      ..addListener(() {
        setState(() {});
      });
  }

  _BackdropState(this.backBar, this.backLayer, this.frontLayer);

  wrapInGestureDetectorIfClosed(
      final BoxConstraints constraints, final Widget child) {
    if (controller.status != AnimationStatus.dismissed) {
      return GestureDetector(
        onVerticalDragUpdate: (dragDetails) {
          controller.value +=
              dragDetails.primaryDelta / (constraints.biggest.height - 100);
          setState(() {});
        },
        onVerticalDragEnd: (dragDetails) {
          final flingVelocity = dragDetails.velocity.pixelsPerSecond.dy /
              constraints.biggest.height;
          print(flingVelocity);

          if (flingVelocity < 0.2 &&
              flingVelocity >= 0.0 &&
              controller.value < 0.2) {
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
        child: child,
      );
    } else
      return child;
  }

  @override
  Widget build(BuildContext context) => LayoutBuilder(
        builder: (context, constraints) {
          final frontLayerTop = backBarHeight +
              controller.value *
                  (constraints.biggest.height -
                      hiddenFrontLayerHeight -
                      backBarHeight);
          final backLayerHeight = constraints.biggest.height - backBarHeight;
          return Stack(
            fit: StackFit.expand,
            children: <Widget>[
              // Background:
              Container(
                color: Theme.of(context).primaryColor,
              ),
              // Back Bar:
              Positioned(
                top: 0.0,
                height: backBarHeight,
                left: 0.0,
                right: 0.0,
                child: GestureDetector(
                  onTap: () {
                    switch (controller.status) {
                      case AnimationStatus.forward:
                      case AnimationStatus.completed:
                        controller.fling(velocity: -.01);
                        break;
                      case AnimationStatus.reverse:
                      case AnimationStatus.dismissed:
                        controller.fling(velocity: 0.01);
                        break;
                    }
                  },
                  child: Material(
                    type: MaterialType.transparency,
                    child: Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: backBar,
                    ),
                  ),
                ),
              ),
              // Back layer:
              Positioned(
                top: backBarHeight,
                height: backLayerHeight,
                left: 0.0,
                right: 0.0,
                child: backLayer,
              ),
              // Front layer:
              Positioned.fill(
                top: frontLayerTop,
                bottom: -frontLayerTop,
                left: 0.0,
                right: 0.0,
                child: wrapInGestureDetectorIfClosed(constraints, frontLayer),
              ),
            ],
          );
        },
      );

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }
}
