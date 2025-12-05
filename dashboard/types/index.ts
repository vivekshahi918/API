export interface Incident {
  id: string;
  serviceName: string;
  endpoint: string;
  type: string; // "SLOW" or "ERROR"
  status: string;
  detectedAt: string;
}
export interface ApiLog {
  id: string;
  serviceName: string;
  endpoint: string;
  method: string;
  status: number;
  durationMs: number;
  timestamp: string;
  errorMessage?: string;
  isRateLimitHit: boolean;
}
