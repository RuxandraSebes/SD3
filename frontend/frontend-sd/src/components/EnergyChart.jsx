// frontend/components/EnergyChart.jsx (NEW FILE)
import React from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

class EnergyChart extends React.Component {
    render() {
        const { data } = this.props;

        const chartData = data.map(item => ({
            // Transformam timestamp-ul inapoi in format 'HH:00'
            hour: new Date(item.hourTimestamp).getHours() + ':00',
            consumption: item.totalConsumption
        })).sort((a, b) => {
            // Sortam numeric dupa ora (ex: 10 inainte de 11, nu alfabetic)
            return parseInt(a.hour) - parseInt(b.hour);
        });

        return (
            <div style={{ width: '100%', height: 300 }}>
                <ResponsiveContainer>
                    <BarChart
                        data={chartData}
                        margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                    >
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis dataKey="hour" />
                        <YAxis label={{ value: 'kWh', angle: -90, position: 'insideLeft' }} />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="consumption" fill="#8884d8" name="Hourly Consumption (kWh)" />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        );
    }
}

export default EnergyChart;