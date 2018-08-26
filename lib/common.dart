import 'package:flutter/material.dart';

Row row(final List<Widget> widgets) => Row(
      children: widgets,
    );

Column column(final List<Widget> widgets) => Column(
      children: widgets,
    );

Stack stack(final List<Widget> widgets) => Stack(
      children: widgets,
      fit: StackFit.expand,
    );

Expanded expanded(final Widget widget) => Expanded(
  child: widget,
);

Center center(final Widget widget) => Center(
  child: widget,
);
