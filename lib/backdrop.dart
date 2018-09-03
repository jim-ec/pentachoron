import 'package:flutter/material.dart';

class Backdrop extends StatefulWidget {
  final Widget backPanel;
  final Widget frontPanel;
  final Widget backBar;

  const Backdrop({
    Key key,
    @required this.backPanel,
    @required this.frontPanel,
    @required this.backBar,
  }) : super(key: key);

  @override
  _BackdropState createState() =>
      _BackdropState(frontPanel, backPanel, backBar);
}

class _BackdropState extends State<Backdrop> {
  final Widget backPanel;
  final Widget frontPanel;
  final Widget backBar;
  
  static const backBarHeight = 56.0;

  _BackdropState(this.frontPanel, this.backPanel, this.backBar);

  @override
  Widget build(BuildContext context) => LayoutBuilder(
        builder: (context, constraints) {
          final frontPanelTop = constraints.biggest.height - 64.0;
          final backPanelHeight = constraints.biggest.height - backBarHeight;
          return Stack(
            fit: StackFit.expand,
            children: <Widget>[
              Container(
                color: Theme.of(context).primaryColor,
              ),
              Positioned(
                top: 0.0,
                height: backBarHeight,
                left: 0.0,
                right: 0.0,
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: backBar,
                ),
              ),
              Positioned(
                top: backBarHeight,
                height: backPanelHeight,
                left: 0.0,
                right: 0.0,
                child: backPanel,
              ),
              Positioned.fill(
                top: frontPanelTop,
                bottom: -frontPanelTop,
                left: 0.0,
                right: 0.0,
                child: frontPanel,
              ),
            ],
          );
        },
      );
}
