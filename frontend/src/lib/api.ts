import axios from "axios";

export const api = axios.create({
  baseURL: "/api",
  headers: { "Content-Type": "application/json" },
});

export interface PhoneAnalyzeResponse {
  rawInput: string;
  valid: boolean;
  e164: string | null;
  country: string | null;
  region: string | null;
  carrier: string | null;
  lineType: string | null;
  timezones: string[];
  regionLat: number | null;
  regionLng: number | null;
  regionName: string | null;
  lada: string | null;
  error: string | null;
}

export interface PhoneQuery {
  id: number;
  rawInput: string;
  e164: string | null;
  country: string | null;
  region: string | null;
  carrier: string | null;
  lineType: string | null;
  lada: string | null;
  valid: boolean;
  createdAt: string;
}