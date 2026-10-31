# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

1. Business goal

Allocate costs accurately so Finance can report profitability and Operations can control spending.

2. Stakeholders

- Finance, Accounting, Warehouse/Store Operations, Supply Chain, and Data/System owners.

3. Information needed

- Cost categories, source systems, data owners, and reporting frequency.
- Direct costs versus shared costs and their allocation drivers.
- Required granularity: warehouse, store, product, region, or cost center.

4. Key questions

- Which decisions must the allocation support?
- Who owns each rule, and which drivers should be used?
- How are transfers, corrections, and reconciliation with the ledger handled?

5. Challenges and risks

- Inconsistent identifiers or definitions across systems.
- Missing or unreliable allocation drivers.
- Double counting and changes that incorrectly restate history.

6. Proposed approach

Use stable identifiers, effective-dated mappings, documented rules, and traceable calculations.
Start with material costs and reconcile allocated totals with the financial ledger.

7. Success measures

- Reconciliation within an agreed tolerance.
- Fewer manual close adjustments.
- Traceable variances and actionable unit-cost metrics.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

1. Business goal

Reduce total fulfillment cost without reducing service quality.

2. Stakeholders

- Finance, Warehouse Operations, Supply Chain, Transportation, Customer Service, and Data teams.

3. Information needed

- Unit costs, demand, utilization, productivity, transport rates, inventory levels, and SLAs.
- Current bottlenecks, contracts, seasonality, and planned investments.

4. Key questions

- Which cost drivers and bottlenecks have the greatest impact?
- Which service levels are non-negotiable?
- Will savings reduce total cost or only move it to another team?

5. Challenges and risks

- Weak baselines, seasonal variation, hidden implementation cost, and service degradation.

6. Proposed approach

Rank initiatives by risk-adjusted value and SLA impact. Pilot options such as inventory placement,
shipment consolidation, labor scheduling, slotting, or automation, then scale proven changes.

7. Success measures

- Lower cost per order/pallet while maintaining delivery, stock-out, and quality targets.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

1. Business goal

Provide timely operational cost visibility while keeping Finance as the source of truth.

2. Stakeholders

- Finance, Accounting, Operations, Integration, Security, Audit, and source-system owners.

3. Information needed

- Systems of record, data contracts, volumes, latency, close schedules, and correction workflows.

4. Key questions

- Which system owns each value, and what must be real time?
- How are failures, duplicates, corrections, and reconciliation handled?
- What security, audit, and retention rules apply?

5. Challenges and risks

- Identifier mismatches, duplicate/out-of-order events, outages, and unreconciled balances.

6. Proposed approach

Use versioned contracts, immutable IDs, idempotency keys, and an outbox for reliable events. Support
retries and scheduled reconciliation with the financial ledger.

7. Success measures

- Shorter close time, fewer manual corrections, low synchronization latency, and reconciled totals.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

1. Business goal

Forecast fulfillment costs and allocate resources early enough to support decisions.

2. Stakeholders

- Finance, Warehouse/Store Operations, Supply Chain, Commercial, and Data teams.

3. Information needed

- Historical actuals, demand and product mix, labor/carrier rates, utilization, seasonality, and
  planned network changes.

4. Key questions

- What horizon and granularity are required?
- Who owns assumptions, and how often should forecasts refresh?
- Which decisions are triggered by forecast variance?

5. Challenges and risks

- Poor data, uncertain demand, biased assumptions, and uncontrolled budget versions.

6. Proposed approach

Use driver-based forecasts, versioned assumptions, scenarios, and rolling reforecasts. Compare
actual, budget, and forecast values with clear variance explanations.

7. Success measures

- Improved forecast accuracy, reduced bias, faster planning, and explainable variances.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

1. Business goal

Complete the replacement within budget while preserving cost history and service continuity.

2. Stakeholders

- Finance, Accounting, Program Management, Warehouse Operations, Supply Chain, and IT.

3. Information needed

- Historical costs, replacement budget, overlap period, asset treatment, inventory-transfer plan,
  contracts, and expected productivity ramp.

4. Key questions

- Which costs are one-time versus recurring?
- How will cost centers and budgets change at cutover?
- What contingency and approval thresholds are required?

5. Challenges and risks

- Dual-running cost, disruption, incorrect cost attribution, and losing history when reusing the
  business-unit code.

6. Proposed approach

Keep separate immutable IDs for old and new warehouses with effective dates. Separate transition
and run budgets, and track commitments, actuals, forecast-at-completion, and service impact.

7. Success measures

- Budget variance, service continuity, preserved audit history, and realized replacement benefits.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
