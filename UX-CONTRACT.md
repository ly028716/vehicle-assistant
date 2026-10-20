# UX Contract — Vehicle Assistant

## Product context

- Audience: individual Chinese-speaking vehicle owners.
- Primary jobs: maintain a single vehicle profile, add service history, understand due maintenance, inspect local-only data, export it, or delete it.
- Active locale: `zh-CN`; dates are entered as ISO `yyyy-MM-dd`; amounts are shown as yuan.
- Accessibility target: Android/Compose native semantics with WCAG 2.2 AA-equivalent contrast and touch targets.

## Business-context sources

| Domain / scope | Authoritative source | Source type |
|---|---|---|
| Product capability and privacy boundary | `README.md` | maintained product brief |
| Reminder, deletion, export and assistant rules | `../docs/superpowers/specs/2026-09-01-offline-vehicle-assistant-portfolio-design.md` | product specification |

## Canonical UI Map

| Capability | Canonical owner | Allowed variants | Verification |
|---|---|---|---|
| Select/Listbox | Material 3 `ExposedDropdownMenuBox` | authored popup | tap, selection, dismissal |
| Date | typed `OutlinedTextField` | `yyyy-MM-dd` | validation unit tests |
| Form | screen ViewModel + `OutlinedTextField` | create / edit | ViewModel tests |
| Dialog | Material 3 `AlertDialog` | destructive confirm | UI/device test |
| CRUD | screen ViewModel and repository | return to source page | repository + ViewModel tests |

## Flow ledger

| Operation | Pending | Success | Failure recovery |
|---|---|---|---|
| Save vehicle / record | disable commit, retain input | inline confirmation; remain on workflow | inline error, preserve input |
| Edit record | retain editable values | refresh history in place | inline error, preserve input |
| Delete record | confirmation dialog | refresh history in place | dialog closes, error is visible inline |
| Export data | create-document picker | system-selected local file | inline error, no data leaves app without a chosen URI |
| Delete all data | explicit confirmation | return to empty home | inline error, data remains |
| Ask assistant | send is disabled | append local answer | inline error, preserve prior messages |

## Feedback and resilience

- No network is used; all screens operate against local Room data.
- Mutations are pessimistic and duplicate submissions are disabled while saving/loading.
- Delete confirmation names the affected data and its permanent consequence.
- Assistant answers declare the local-only/no-diagnosis boundary before use.
