"use client";

import { useEffect, useState } from "react";
import axios from "axios";
import { useRouter } from "next/navigation"; // Import Router
import { ApiLog, Incident } from "@/types";
import { AlertTriangle, CheckCircle, Clock, XCircle, Activity, Zap, CheckSquare, LogOut } from "lucide-react";
import { format } from "date-fns";

export default function Dashboard() {
  const [logs, setLogs] = useState<ApiLog[]>([]);
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Filters
  const [filterService, setFilterService] = useState("ALL");
  const [filterStatus, setFilterStatus] = useState("ALL");

  const router = useRouter();

  // 🔥 Helper to get headers with Token
  const getAuthHeader = () => {
    const token = localStorage.getItem("jwt_token");
    return { headers: { Authorization: `Bearer ${token}` } };
  };

  const fetchData = async () => {
    try {
      // 1. Check if token exists
      const token = localStorage.getItem("jwt_token");
      if (!token) {
        router.push("/login"); // Kick to login if no token
        return;
      }

      // 2. Fetch Logs (With Token)
      const logRes = await axios.get("http://localhost:8080/api/collector/logs", getAuthHeader());
      const sortedLogs = logRes.data.sort((a: ApiLog, b: ApiLog) => 
        new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
      );
      setLogs(sortedLogs);

      // 3. Fetch Incidents (With Token)
      const incidentRes = await axios.get("http://localhost:8080/api/collector/incidents", getAuthHeader());
      setIncidents(incidentRes.data);
      setLoading(false);

    } catch (error: any) {
      console.error("Failed to fetch data", error);
      // If token is invalid/expired (403 or 401), force logout
      if (error.response?.status === 403 || error.response?.status === 401) {
        localStorage.removeItem("jwt_token");
        router.push("/login");
      }
    }
  };

  const resolveIncident = async (id: string) => {
    try {
      await axios.post(`http://localhost:8080/api/collector/incidents/${id}/resolve`, {}, getAuthHeader());
      alert("Incident Resolved!");
      fetchData();
    } catch (error) {
      alert("Failed to resolve.");
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("jwt_token");
    router.push("/login");
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 5000);
    return () => clearInterval(interval);
  }, []);

  // Metrics
  const totalRequests = logs.length;
  const slowRequests = logs.filter(l => l.durationMs > 500).length;
  const errorRequests = logs.filter(l => l.status >= 500).length;
  const uniqueServices = Array.from(new Set(logs.map(l => l.serviceName)));

  // Filter Logic
  const filteredLogs = logs.filter(log => {
    if (filterService !== "ALL" && log.serviceName !== filterService) return false;
    if (filterStatus === "ERROR" && log.status < 500) return false;
    if (filterStatus === "SLOW" && log.durationMs <= 500) return false;
    return true;
  });

  return (
    <div className="min-h-screen bg-gray-50 p-8 font-sans text-gray-900">
      <div className="max-w-7xl mx-auto">
        <header className="mb-8 flex justify-between items-center">
          <div>
            <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-2">
                <Activity className="text-blue-600" /> API Observability
            </h1>
            <p className="text-gray-500 mt-1">Live monitoring & Incident Management</p>
          </div>
          <button onClick={handleLogout} className="flex items-center gap-2 text-gray-600 hover:text-red-600 bg-white border border-gray-300 px-4 py-2 rounded-lg transition">
            <LogOut className="h-4 w-4" /> Logout
          </button>
        </header>

        {/* 📊 WIDGETS */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 flex justify-between">
            <div><p className="text-sm text-gray-500 uppercase">Total Traffic</p><p className="text-3xl font-bold mt-1">{totalRequests}</p></div>
            <div className="p-3 bg-blue-50 text-blue-600 rounded-full"><Zap className="h-6 w-6" /></div>
          </div>
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 flex justify-between">
            <div><p className="text-sm text-gray-500 uppercase">Slow Operations</p><p className="text-3xl font-bold text-yellow-600 mt-1">{slowRequests}</p></div>
            <div className="p-3 bg-yellow-50 text-yellow-600 rounded-full"><Clock className="h-6 w-6" /></div>
          </div>
          <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-200 flex justify-between">
            <div><p className="text-sm text-gray-500 uppercase">Server Errors</p><p className="text-3xl font-bold text-red-600 mt-1">{errorRequests}</p></div>
            <div className="p-3 bg-red-50 text-red-600 rounded-full"><AlertTriangle className="h-6 w-6" /></div>
          </div>
        </div>

        {/* 🚨 ACTIVE INCIDENTS SECTION */}
        {incidents.length > 0 && (
          <div className="mb-8 bg-white shadow-sm rounded-lg border border-red-200">
            <div className="px-6 py-4 bg-red-50 border-b border-red-200 flex justify-between items-center">
              <h2 className="font-bold text-red-800 flex items-center gap-2">
                <AlertTriangle className="h-5 w-5" /> Active Incidents ({incidents.length})
              </h2>
            </div>
            <div className="divide-y divide-gray-100">
              {incidents.map((incident) => (
                <div key={incident.id} className="px-6 py-4 flex justify-between items-center hover:bg-gray-50">
                  <div>
                    <p className="font-semibold text-gray-800">{incident.serviceName} &rarr; <span className="font-mono text-sm">{incident.endpoint}</span></p>
                    <p className="text-sm text-red-600 font-medium">Issue: {incident.type} API detected</p>
                    <p className="text-xs text-gray-400 mt-1">Detected at: {format(new Date(incident.detectedAt), "HH:mm:ss")}</p>
                  </div>
                  <button 
                    onClick={() => resolveIncident(incident.id)}
                    className="flex items-center gap-1 bg-white border border-green-600 text-green-700 hover:bg-green-50 px-4 py-2 rounded-md text-sm font-semibold transition"
                  >
                    <CheckSquare className="h-4 w-4" /> Mark Resolved
                  </button>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 📝 LOGS TABLE */}
        <div className="bg-white shadow-sm rounded-lg overflow-hidden border border-gray-200">
          <div className="px-6 py-4 border-b border-gray-200 bg-gray-50 flex flex-col sm:flex-row justify-between items-center gap-4">
            <h2 className="font-semibold text-gray-700">Live Logs</h2>
            <div className="flex gap-3">
              <select className="border-gray-300 border rounded-md text-sm px-3 py-2 outline-none" onChange={(e) => setFilterService(e.target.value)}>
                <option value="ALL">All Services</option>
                {uniqueServices.map(s => <option key={s} value={s}>{s}</option>)}
              </select>
              <select className="border-gray-300 border rounded-md text-sm px-3 py-2 outline-none" onChange={(e) => setFilterStatus(e.target.value)}>
                <option value="ALL">All Status</option>
                <option value="SLOW">Slow Only</option>
                <option value="ERROR">Errors Only</option>
              </select>
            </div>
          </div>
          
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Service</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Endpoint</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Latency</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Time</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {loading ? (
                  <tr><td colSpan={6} className="px-6 py-4 text-center">Loading...</td></tr>
                ) : filteredLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                        {log.status >= 500 ? <XCircle className="text-red-500 h-5 w-5"/> : 
                         log.durationMs > 500 ? <AlertTriangle className="text-yellow-500 h-5 w-5"/> : 
                         <CheckCircle className="text-green-500 h-5 w-5"/>}
                    </td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">{log.serviceName}</td>
                    <td className="px-6 py-4 text-sm font-mono text-gray-500">{log.endpoint}</td>
                    <td className="px-6 py-4 text-sm font-bold text-gray-600">{log.durationMs}ms</td>
                    <td className="px-6 py-4 text-sm text-gray-500">{format(new Date(log.timestamp), "HH:mm:ss")}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}