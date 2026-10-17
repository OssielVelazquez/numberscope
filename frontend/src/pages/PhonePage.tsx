import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { api } from "../lib/api";
import type { PhoneAnalyzeResponse, PhoneQuery } from "../lib/api";

const REGIONS = ["MX", "US", "ES", "AR", "CO", "BR", "GB", "FR", "DE"];

export default function PhonePage() {
  const [raw, setRaw] = useState("");
  const [region, setRegion] = useState("MX");
  const [result, setResult] = useState<PhoneAnalyzeResponse | null>(null);
  const [filter, setFilter] = useState("");
  const queryClient = useQueryClient();

  const historyQuery = useQuery({
    queryKey: ["phone-history"],
    queryFn: async () => (await api.get<PhoneQuery[]>("/phone/history")).data,
  });

  const analyzeMutation = useMutation({
    mutationFn: async (payload: { rawInput: string; defaultRegion: string }) =>
      (await api.post<PhoneAnalyzeResponse>("/phone/analyze", payload)).data,
    onSuccess: (data) => {
      setResult(data);
      queryClient.invalidateQueries({ queryKey: ["phone-history"] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/phone/history/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["phone-history"] });
    },
  });

  const clearAllMutation = useMutation({
    mutationFn: async () => {
      await api.delete(`/phone/history`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["phone-history"] });
    },
  });

  const handleAnalyze = () => {
    if (!raw.trim()) return;
    analyzeMutation.mutate({ rawInput: raw, defaultRegion: region });
  };

  const handleHistoryClick = (entry: PhoneQuery) => {
    setRaw(entry.rawInput);
    analyzeMutation.mutate({
      rawInput: entry.rawInput,
      defaultRegion: entry.country ?? region,
    });
  };

  const handleDelete = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    if (confirm("¿Eliminar esta consulta del historial?")) {
      deleteMutation.mutate(id);
    }
  };

  const handleClearAll = () => {
    if (
      confirm(
        "¿Borrar todo el historial? Esta acción no se puede deshacer."
      )
    ) {
      clearAllMutation.mutate();
    }
  };

  const filteredHistory = useMemo(() => {
    const data = historyQuery.data ?? [];
    const q = filter.trim().toLowerCase();
    if (!q) return data;
    return data.filter((h) => {
      return (
        h.rawInput.toLowerCase().includes(q) ||
        (h.country ?? "").toLowerCase().includes(q) ||
        (h.lineType ?? "").toLowerCase().includes(q) ||
        (h.lada ?? "").toLowerCase().includes(q)
      );
    });
  }, [historyQuery.data, filter]);

  const totalCount = historyQuery.data?.length ?? 0;

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <section className="lg:col-span-2 bg-slate-800/40 rounded-xl border border-slate-700 p-6">
        <h2 className="text-xl font-semibold mb-4">Análisis de número</h2>

        <div className="flex flex-col sm:flex-row gap-3">
          <input
            value={raw}
            onChange={(e) => setRaw(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleAnalyze()}
            placeholder="+52 55 1234 5678"
            className="flex-1 bg-slate-900 border border-slate-700 rounded-md px-3 py-2 focus:outline-none focus:border-blue-500"
          />
          <select
            value={region}
            onChange={(e) => setRegion(e.target.value)}
            className="bg-slate-900 border border-slate-700 rounded-md px-3 py-2"
          >
            {REGIONS.map((r) => (
              <option key={r} value={r}>
                {r}
              </option>
            ))}
          </select>
          <button
            onClick={handleAnalyze}
            disabled={!raw || analyzeMutation.isPending}
            className="bg-blue-600 hover:bg-blue-500 disabled:opacity-50 rounded-md px-5 py-2 font-medium transition"
          >
            {analyzeMutation.isPending ? "Analizando..." : "Analizar"}
          </button>
        </div>

        {result && (
          <>
            <div className="mt-6 grid grid-cols-2 gap-3 text-sm">
              <Field label="Válido" value={result.valid ? "Sí" : "No"} />
              <Field label="E.164" value={result.e164 ?? "-"} />
              <Field label="País" value={result.country ?? "-"} />
              <Field label="Región (ISO)" value={result.region ?? "-"} />
              <Field label="Operador" value={result.carrier ?? "-"} />
              <Field label="Tipo de línea" value={result.lineType ?? "-"} />
              <Field label="LADA" value={result.lada ?? "-"} />
              <Field
                label="Bloque de numeración"
                value={result.regionName ?? "-"}
              />
              {result.error && (
                <div className="col-span-2 text-amber-400 text-xs mt-2">
                  {result.error}
                </div>
              )}
            </div>

            {result.regionLat !== null && result.regionLng !== null && (
              <div className="mt-6">
                <h3 className="text-sm font-semibold mb-2 text-slate-300">
                  Región del bloque de numeración (LADA {result.lada})
                </h3>
                <div className="rounded-md overflow-hidden border border-slate-700">
                  <MapContainer
                    center={[result.regionLat, result.regionLng]}
                    zoom={9}
                    style={{ height: "280px", width: "100%" }}
                    key={`${result.regionLat}-${result.regionLng}`}
                  >
                    <TileLayer
                      attribution="&copy; OpenStreetMap"
                      url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    />
                    <Marker position={[result.regionLat, result.regionLng]}>
                      <Popup>
                        <strong>{result.regionName}</strong>
                        <br />
                        Región asignada al bloque del número.
                        <br />
                        <em>No es la ubicación del teléfono.</em>
                      </Popup>
                    </Marker>
                  </MapContainer>
                </div>
                <p className="text-xs text-slate-500 mt-2 leading-relaxed">
                  Esta región corresponde al bloque de numeración asignado por
                  el IFT. No representa la ubicación actual del dispositivo ni
                  del propietario.
                </p>
              </div>
            )}

            <div className="text-xs text-slate-500 mt-4">
              Los datos provienen del rango público del número. El análisis no
              revela la ubicación del propietario.
            </div>
          </>
        )}
      </section>

      <aside className="bg-slate-800/40 rounded-xl border border-slate-700 p-6 flex flex-col">
        <div className="flex items-center justify-between mb-3">
          <h3 className="text-lg font-semibold">
            Historial {totalCount > 0 && (
              <span className="text-sm text-slate-500 font-normal">
                ({totalCount})
              </span>
            )}
          </h3>
          {totalCount > 0 && (
            <button
              onClick={handleClearAll}
              disabled={clearAllMutation.isPending}
              className="text-xs text-red-400 hover:text-red-300 disabled:opacity-50 transition"
              title="Borrar todo el historial"
            >
              {clearAllMutation.isPending ? "Borrando..." : "Borrar todo"}
            </button>
          )}
        </div>

        <input
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          placeholder="Filtrar por número, país o LADA..."
          className="w-full bg-slate-900 border border-slate-700 rounded-md px-3 py-2 mb-3 text-sm focus:outline-none focus:border-blue-500"
        />

        <div className="space-y-2 max-h-[65vh] overflow-auto text-sm">
          {filteredHistory.map((h) => (
            <div
              key={h.id}
              onClick={() => handleHistoryClick(h)}
              className="group relative border border-slate-700 hover:border-blue-500 hover:bg-slate-800/60 rounded-md p-3 transition cursor-pointer"
              title="Clic para volver a analizar"
            >
              <button
                onClick={(e) => handleDelete(e, h.id)}
                className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 text-slate-500 hover:text-red-400 transition text-lg leading-none"
                title="Eliminar esta consulta"
              >
                ×
              </button>

              <div className="font-mono text-xs text-slate-400">
                {new Date(h.createdAt).toLocaleString("es-MX")}
              </div>
              <div className="font-medium mt-1">{h.rawInput}</div>
              <div className="flex items-center gap-2 mt-1">
                <span className="text-slate-400 text-xs">
                  {h.country ?? "-"} · {h.lineType ?? "-"}
                </span>
                {h.lada && (
                  <span className="text-xs bg-slate-700 text-slate-300 rounded px-2 py-0.5">
                    LADA {h.lada}
                  </span>
                )}
              </div>
            </div>
          ))}

          {filteredHistory.length === 0 && totalCount === 0 && (
            <div className="text-slate-500 text-sm">
              Sin consultas todavía.
            </div>
          )}

          {filteredHistory.length === 0 && totalCount > 0 && (
            <div className="text-slate-500 text-sm">
              No hay resultados para "{filter}".
            </div>
          )}
        </div>
      </aside>
    </div>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="bg-slate-900/60 border border-slate-700 rounded-md px-2 py-2">
      <div className="text-xs text-slate-500">{label}</div>
      <div className="font-medium text-sm">{value}</div>
    </div>
  );
}