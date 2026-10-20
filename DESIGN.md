---
version: "1.0"
name: "Vehicle Assistant"
description: "A calm, offline-first road log for a single vehicle."
colors:
  primary: "#2F6B45"
  primaryContainer: "#E7EFE4"
  warning: "#B46B39"
  warningContainer: "#FFECDD"
  background: "#FFFBF4"
  surface: "#FFFFFEFB"
  surfaceVariant: "#F1F3E9"
  ink: "#202A25"
  muted: "#6D716D"
  outline: "#E6E0D7"
typography:
  sans:
    fontFamily: "Android system sans-serif with CJK fallback"
rounded:
  sm: "12dp"
  md: "18dp"
  lg: "24dp"
  xl: "28dp"
spacing:
  screen: "20dp"
  section: "16dp"
components:
  material3: {}
  cards: {}
  forms: {}
  dialogs: {}
---

# Vehicle Assistant design system

## Overview

### Creative North Star

The confidence of a well-kept vehicle service booklet: practical records, a clear status marker, and just enough warmth to make routine maintenance feel manageable.

### Product context and register

- **Audience and primary job:** Chinese-speaking individual vehicle owners recording service history and understanding their own local data.
- **Target market(s) and evidence:** Chinese market; the maintained product brief and all user-facing product copy are Chinese.
- **Locale:** `zh-CN`, with Android system typography and `Locale.CHINA` for mileage grouping.
- **Usage scene:** One-handed, intermittent use on a phone—often around a service visit—so primary actions stay explicit and touchable.
- **Register:** Product utility with a small amount of reassurance, never an automotive dashboard simulation.
- **Memorable signature:** A quiet, full-bleed vehicle portrait makes the car the protagonist; one precise maintenance progress line turns the next action into a legible road signal.
- **Restraint:** Forms, records, and privacy actions remain quiet, direct, and highly scannable.
- **Anti-references:** Avoid dark “sport mode” dashboards, faux telemetry, chrome effects, and generic finance-app statistic grids.
- **Token ownership/runtime mapping:** `ui/theme/Color.kt`, `Theme.kt`, and `Type.kt` are the runtime source of truth; this file documents their semantic roles.

## Colors

Road green is the primary action and healthy-status color. Warm white is the canvas and card surface; pale green is reserved for supportive surfaces and icon nests. Amber communicates upcoming or overdue maintenance. Ink, muted ink, and a soft warm divider create the content hierarchy. Destructive states use Material 3 error colors and must always include clear text.

## Typography

Use Android’s system sans-serif/CJK fallback for comfortable Chinese reading. Page titles are `headlineMedium`; primary card labels use `titleLarge`; values use `displaySmall` only when they are the screen’s core fact. Never use all caps or condensed display type.

## Layout

Screens use 20dp horizontal gutters, 16dp section rhythm, 12–14dp local grouping, and a bottom navigation safe area supplied by `Scaffold`. Lists scroll as the page; forms preserve entered content during validation and save. Cards never rely on image content alone to communicate an action.

## Elevation & shapes

Hierarchy is primarily tonal and outlined rather than shadow-heavy. Interactive cards use visible soft outlines. Corners are restrained: 20dp only for the vehicle hero and 8–16dp for forms, lists, and supporting surfaces.

## Components

Material 3 is the canonical component owner. Native Compose controls provide focus, touch targets, selected, disabled, busy, and semantic states. Primary buttons carry commits; outlined buttons are secondary; destructive actions remain text/outlined until final confirmation. Alerts and confirmations use `AlertDialog`; success and error feedback is shown inline close to the affected workflow.

## Do's and Don'ts

- **Do:** make record type, date, mileage, and cost individually scannable.
- **Do:** preserve the same add/edit/delete vocabulary across maintenance and repair.
- **Don't:** use warning color for ordinary navigation or decoration.
- **Don't:** place an irreversible delete next to the primary productive action.
